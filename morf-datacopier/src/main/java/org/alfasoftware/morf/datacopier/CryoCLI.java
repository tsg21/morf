package org.alfasoftware.morf.datacopier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.alfasoftware.morf.datacopier.model.CommandLineUtils;
import org.alfasoftware.morf.dataset.DataSetConsumer;
import org.alfasoftware.morf.dataset.DataSetProducer;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * CLI version of Cryo.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2012
 */
public class CryoCLI {

  /**
   * Indicates whether we should show progress on the console (--progress).
   */
  private boolean withProgress = false;

  /**
   * Start the program.
   *
   * @param args Command line arguments to the program.
   */
  public static void main(String[] args) {
    new CryoCLI().run(args);
  }


  /**
   * Run the command line Cryo.
   *
   * @param args command line arguments to the program.
   */
  public void run(String[] args) {
    List<String> argList = parseArgs(args);

    // Check the arguments again
    if (argList.size() != 1) {
      help();
      System.exit(1);
    }

    // Load the properties
    Properties properties = null;
    try {
      properties = loadProperties(argList.get(0));
    } catch (Exception e) {
      System.err.println("Failed to load properties:\n" + e.getMessage());
      System.exit(1);
    }

    copyData(properties);
  }


  /**
   * Parses the command line arguments and interprets the flags. Flags are removed and then
   * the remaining arguments are returned as a list.
   *
   * @param args the arguments
   * @return the list of arguments after all the flags are removed.
   */
  private List<String> parseArgs(String[] args) {
    List<String> argList = new LinkedList<>(Arrays.asList(args));

    int i = 0;
    while (i < argList.size()) {
      String thisArg = argList.get(i);

      if (thisArg.trim().equalsIgnoreCase("--progress")) {
        withProgress = true;
        argList.remove(i);
        continue;
      }

      i++;
    }

    return argList;
  }


  /**
   * Copies the data as per the instructions in the properties file.
   *
   * @param properties the properties file.
   */
  private void copyData(Properties properties) {
    DataSetProducer producer = CommandLineUtils.createProducer(properties);
    DataSetConsumer consumer = CommandLineUtils.createConsumer(properties);

    CryoRunnable runnable = new CryoRunnable(producer, consumer);
    try {
      runnable.run(withProgress ? new CommandLineUtils.ProgressMonitor() : new NullProgressMonitor());
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }


  /**
   * Loads the execution properties from the supplied file.
   *
   * @param filename the name of the file containing the properties.
   * @return the properties.
   */
  private Properties loadProperties(String filename) {
    Properties props = new Properties();
    FileInputStream fis;
    try {
      fis = new FileInputStream(new File(filename));
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("Could not file properties file [" + filename + "]", e);
    }

    try {
      props.load(fis);

      fis.close();
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to load the properties file [" + filename + "]", e);
    }
    return props;
  }


  /**
   * Displays the help text associated with this command.
   */
  private void help() {
    String helpText =
      "Usage: java -jar cryo.jar com.chpconsulting.cryo.CryoCLI [OPTIONS] FILE\n\n" +
      "OPTIONS\n" +
      "  --progress Show progress indicator.\n\n" +
      "FILE\n" +
      "  The properties file is a standard Java properties file such as the following:\n" +
      "    source.type=DATABASE\n" +
      "    source.database.url=jdbc:mysql://localhost:3306/alfa\n" +
      "    source.database.username=test\n" +
      "    source.database.password=test\n" +
      "    source.database.schemaName=alfaSchema\n" +
      "    destination.type=XML\n" +
      "    destination.xml.file=c:\\SomeDirectory\\blah.zip\n\n" +
      "  Source / Destination types:\n" +
      "    DATABASE\n" +
      "    XML\n" +
      "    EXCEL (NOT CURRENTLY SUPPORTED)\n\n" +
      "  To extract to / from a directory rather than a ZIP file, populate the 'xxx.xml.directory' instead of 'xxx.xml.file'\n";

    System.out.println(helpText);
  }
}
