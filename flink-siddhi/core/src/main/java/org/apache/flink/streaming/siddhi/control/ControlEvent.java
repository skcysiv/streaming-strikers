/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.siddhi.control;

import java.io.Serializable;
import java.util.Date;

public abstract class ControlEvent implements Serializable {
    public static final String DEFAULT_INTERNAL_CONTROL_STREAM = "_internal_control_stream";
    private final Date createdTime = new Date();
    private Date expiredTime;
    private boolean expired;

    private long sendToPartitionNumber = -1;

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void expire() {
        this.expired = true;
        this.expiredTime = new Date();
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public long getSendToPartitionNumber() {
        return sendToPartitionNumber;
    }

    public void setSendToPartitionNumber(long sendToPartitionNumber) {
        this.sendToPartitionNumber = sendToPartitionNumber;
    }
}