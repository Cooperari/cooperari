//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.cooperari.core.scheduling;

import java.util.List;

/**
 * Program state factory.
 * 
 * @since 0.2
 */
public enum CProgramStateFactory {

  /**
   * Raw program-state factory.
   * This creates program states associated to individual threads.
   */
  RAW {
    @Override
    public CProgramState create(List<? extends CThreadHandle> readyThreads,
        List<? extends CThreadHandle> blockedThreads) {
      return new CRawProgramState(readyThreads, blockedThreads);
    }
  },
  /**
   * "Thread-group" program-state factory.
   * This creates program states abstracting the state of threads in groups.
   */
  GROUP {
    @Override
    public CProgramState create(List<? extends CThreadHandle> readyThreads,
        List<? extends CThreadHandle> blockedThreads) {
      return new CGroupProgramState(readyThreads, blockedThreads);
    }
  };
  /**
   * Create a new program state.
   * @param readyThreads List of ready threads.
   * @param blockedThreads List of blocked threads.
   * @return A new program state.
   */
  public abstract CProgramState create(List<? extends CThreadHandle> readyThreads, List<? extends CThreadHandle> blockedThreads);

}
