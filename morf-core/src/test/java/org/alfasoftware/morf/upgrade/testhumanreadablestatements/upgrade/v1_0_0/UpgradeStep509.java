/* Copyright 2017 Alfa Financial Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfasoftware.morf.upgrade.testhumanreadablestatements.upgrade.v1_0_0;

import org.alfasoftware.morf.upgrade.DataEditor;
import org.alfasoftware.morf.upgrade.SchemaEditor;
import org.alfasoftware.morf.upgrade.Sequence;
import org.alfasoftware.morf.upgrade.UpgradeStep;

/**
 * A dummy 5.0.9 upgrade step.
 *
 * @author Copyright (c) Alfa Financial Software 2010
 */
@Sequence(5009)
public class UpgradeStep509 implements UpgradeStep {

  /**
   * @see org.alfasoftware.morf.upgrade.UpgradeStep#getDescription()
   */
  @Override
  public String getDescription() {
    return "5.0.9 Upgrade Step";
  }


  /**
   * @see org.alfasoftware.morf.upgrade.UpgradeStep#getJiraId()
   */
  @Override
  public String getJiraId() {
    return "SAMPLE-6";
  }


  /**
   * @see org.alfasoftware.morf.upgrade.UpgradeStep#execute(org.alfasoftware.morf.upgrade.SchemaEditor, org.alfasoftware.morf.upgrade.DataEditor)
   */
  @Override
  public void execute(SchemaEditor schema, DataEditor data) {
    // dummy step
  }
}
