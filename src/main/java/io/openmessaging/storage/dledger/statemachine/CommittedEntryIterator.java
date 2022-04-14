/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openmessaging.storage.dledger.statemachine;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import io.openmessaging.storage.dledger.entry.DLedgerEntry;
import io.openmessaging.storage.dledger.store.DLedgerStore;

/**
 * The iterator implementation of committed entries.
 */
public class CommittedEntryIterator implements Iterator<DLedgerEntry> {

    private final DLedgerStore dLedgerStore;
    private final long         committedIndex;
    private final AtomicLong   applyingIndex;
    private long               currentIndex;

    public CommittedEntryIterator(final DLedgerStore dLedgerStore, final long committedIndex,
                                  final AtomicLong applyingIndex, final long lastAppliedIndex) {
        this.dLedgerStore = dLedgerStore;
        this.committedIndex = committedIndex;
        this.applyingIndex = applyingIndex;
        this.currentIndex = lastAppliedIndex;
    }

    @Override
    public boolean hasNext() {
        return this.currentIndex < this.committedIndex;
    }

    @Override
    public DLedgerEntry next() {
        ++this.currentIndex;
        if (this.currentIndex <= this.committedIndex) {
            final DLedgerEntry dLedgerEntry = this.dLedgerStore.get(this.currentIndex);
            this.applyingIndex.set(this.currentIndex);
            return dLedgerEntry;
        }
        return null;
    }

    public long getIndex() {
        return this.currentIndex;
    }
}