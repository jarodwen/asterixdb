/*
 * Copyright 2009-2010 by The Regents of the University of California
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License from
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.asterix.transaction.management.service.transaction;

import edu.uci.ics.asterix.transaction.management.exception.ACIDException;

/**
 * Provides APIs for managing life cycle of a transaction, that is beginning a
 * transaction and aborting/committing the transaction.
 */

public interface ITransactionManager {

    /**
     * A transaction may be in any of the following states ACTIVE: The
     * transaction is ongoing and has not yet committed/aborted. COMMITTD: The
     * transaction has committed. ABORTED: The transaction has aborted.
     * TIMED_OUT: The transaction has timed out waiting to acquire a lock.
     */
    public enum TransactionState {
        ACTIVE,
        COMMITTED,
        ABORTED,
        TIMED_OUT,
    };

    /**
     * Begins a transaction identified by a transaction id and returns the
     * associated transaction context.
     * 
     * @param jobId
     *            a unique value for the transaction id.
     * @return the transaction context associated with the initiated transaction
     * @see TransactionContext
     * @throws ACIDException
     */
    public TransactionContext beginTransaction(JobId jobId) throws ACIDException;

    /**
     * Returns the transaction context of an active transaction given the
     * transaction id.
     * 
     * @param jobId
     *            a unique value for the transaction id.
     * @return
     * @throws ACIDException
     */
    public TransactionContext getTransactionContext(JobId jobId) throws ACIDException;

    /**
     * Commits a transaction.
     * 
     * @param txnContext
     *            the transaction context associated with the transaction
     * @param datasetId TODO
     * @param PKHashVal TODO
     * @throws ACIDException
     * @see transactionContextimport edu.uci.ics.hyracks.api.job.JobId;
     * @see ACIDException
     */
    public void commitTransaction(TransactionContext txnContext, DatasetId datasetId, int PKHashVal) throws ACIDException;

    /**
     * Aborts a transaction.
     * 
     * @param txnContext
     *            the transaction context associated with the transaction
     * @param datasetId TODO
     * @param PKHashVal TODO
     * @throws ACIDException
     * @see transactionContext
     * @see ACIDException
     */
    public void abortTransaction(TransactionContext txnContext, DatasetId datasetId, int PKHashVal) throws ACIDException;

    /**
     * Indicates end of all activity for a transaction. In other words, all
     * participating threads in the transaction have completed the intended
     * task.
     * 
     * @param txnContext
     *            the transaction context associated with the transaction
     * @param datasetId TODO
     * @param PKHashVal TODO
     * @param success
     *            indicates the success or failure. The transaction is committed
     *            or aborted accordingly.
     * @throws ACIDException
     */
    public void completedTransaction(TransactionContext txnContext, DatasetId datasetId, int PKHashVal, boolean success) throws ACIDException;

    /**
     * Returns the Transaction Provider for the transaction eco-system. A
     * transaction eco-system consists of a Log Manager, a Recovery Manager, a
     * Transaction Manager and a Lock Manager.
     * 
     * @see TransactionSubsystem
     * @return TransactionProvider
     */
    public TransactionSubsystem getTransactionProvider();

}
