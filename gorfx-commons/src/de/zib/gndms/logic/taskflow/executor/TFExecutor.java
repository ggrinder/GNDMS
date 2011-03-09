package de.zib.gndms.logic.taskflow.executor;
/*
 * Copyright 2008-2011 Zuse Institute Berlin (ZIB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import de.zib.gndms.logic.taskflow.TaskAction;

import java.util.concurrent.*;

/**
 * @author try ma ik jo rr a zib
 * @date 08.03.11  15:08
 * @brief
 */
public class TFExecutor {

    private ExecutorService executor = null;

    public TFExecutor() {
        executor = Executors.newCachedThreadPool();
    }

    public void submit( TaskAction ta ) {
        executor.execute( new TaskActionRunner( ta ) );
    }

    public void shutDown() {
        executor.shutdown();
    }

}
