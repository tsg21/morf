package org.alfasoftware.morf.datacopier;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.alfasoftware.morf.dataset.DataSetAdapter;
import org.alfasoftware.morf.dataset.DataSetConnector;
import org.alfasoftware.morf.dataset.DataSetConsumer;
import org.alfasoftware.morf.dataset.DataSetProducer;
import org.alfasoftware.morf.dataset.Record;
import org.alfasoftware.morf.metadata.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Implements {@link IRunnableWithProgress} to perform cryo tasks linked
 * to a JFace progress monitor.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public class CryoRunnable implements IRunnableWithProgress {
  /***/
  private static final Log log = LogFactory.getLog(CryoRunnable.class);

  /** Data set producer to connect when {@link #run(IProgressMonitor)} is invoked */
  private final DataSetProducer producer;

  /** Data set consumer to connect when {@link #run(IProgressMonitor)} is invoked */
  private final DataSetConsumer consumer;

  /**
   * @param producer Data set producer to connect.
   * @param consumer Data set consumer to connect.
   */
  public CryoRunnable(DataSetProducer producer, DataSetConsumer consumer) {
    super();
    this.producer = producer;
    this.consumer = consumer;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void run(final IProgressMonitor progress) throws InvocationTargetException, InterruptedException {

    progress.setTaskName("Transferring data...");

    // Adapt the consumer so we can track progress
    final DataSetConsumer trackedConsumer = new DataSetAdapter(consumer) {
      /**
       * Flag indicating if we have told the progress monitor to start. We have to use this
       * approach because we cannot count the tables in the producer until is has been opened
       * by the connector.
       */
      private boolean taskStarted;

      /**
       * @see org.alfasoftware.morf.dataset.DataSetAdapter#table(org.alfasoftware.morf.metadata.Table, java.lang.Iterable)
       */
      @Override
      public void table(final Table table, final Iterable<Record> records) {

        if (progress.isCanceled()) {
          throw new RuntimeException("Transfer cancelled");
        }

        progress.setTaskName(table.getName());
        log.debug("Processing table [" + table.getName() + "]");

        super.table(table, new ProgressReportingIterable(records, table, progress));

        if (!taskStarted) {
          progress.beginTask("Transferring data set", producer.getSchema().tableNames().size());
          taskStarted = true;
        }

        progress.worked(1);
      }
    };

    log.info("Starting transfer from [" + producer + "] to [" + consumer + "]");

    // Transfer the data
    long startTime = System.currentTimeMillis();
    new DataSetConnector(producer, trackedConsumer).connect();
    long stopTime = System.currentTimeMillis();

    log.info("Transfer complete - " + (stopTime - startTime) + "ms elapsed.");

    progress.done();
  }


  /**
   * Implementation of Iterable<Record> that reports progress as it reads
   * through large result-sets.
   */
  private class ProgressReportingIterable implements Iterable<Record> {

    /**
     * The source iterable
     */
    private final Iterable<Record> records;
    /**
     * The table being processed
     */
    private final Table            table;
    /**
     * The current record count
     */
    private long                   recordCount;
    /**
     * The progress reporter
     */
    private final IProgressMonitor progress;


    /**
     * Create a new instance.
     *
     * @param records The iterable to wrap.
     * @param table The table being processed.
     * @param progress The progress reporter
     */
    private ProgressReportingIterable(Iterable<Record> records, Table table, IProgressMonitor progress) {
      super();
      this.records = records;
      this.progress = progress;
      this.table = table;
    }


    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Record> iterator() {
      return new Iterator<Record>() {
        private final Iterator<Record> delegate = records.iterator();

        /**
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
          return delegate.hasNext();
        }


        /**
         * @see java.util.Iterator#next()
         */
        @Override
        public Record next() {
          if (progress.isCanceled()) {
            throw new RuntimeException("Transfer cancelled");
          }

          if (++recordCount % 10000 == 0) {
            progress.setTaskName(String.format("%s records transferred: %,d", table.getName(), recordCount));
          }
          return delegate.next();
        }


        /**
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
          delegate.remove();
        }
      };
    }
  }
}
