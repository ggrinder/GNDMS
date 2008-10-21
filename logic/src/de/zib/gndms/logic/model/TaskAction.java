package de.zib.gndms.logic.model;

import de.zib.gndms.model.gorfx.Task;
import de.zib.gndms.model.gorfx.types.TaskState;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;


/**
 * ThingAMagic.
 *
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 15.09.2008 Time: 11:26:48
 */
@SuppressWarnings({ "AbstractMethodCallInConstructor" })
public abstract class TaskAction extends AbstractModelAction<Task, Task>
{
    private TaskExecutionService service;

    private static final class ShutdownTaskActionException extends RuntimeException {
        private static final long serialVersionUID = 2772466358157719820L;

        ShutdownTaskActionException(final Exception eParam) {
            super(eParam);
        }
    }

    protected static class TransitException extends RuntimeException {
        private static final long serialVersionUID = 1101501745642141770L;

        protected final TaskState newState;

        protected TransitException(final @NotNull TaskState newStateParam) {
            super();
            newState = newStateParam;
        }

        protected TransitException(final @NotNull TaskState newStateParam,
                                   final @NotNull RuntimeException cause) {
            super(cause);
            newState = newStateParam;
        }

        public boolean isDemandingAbort() { return false; }
    }

    protected static class FailedException extends TransitException {
        private static final long serialVersionUID = -4220356706557491625L;

        protected FailedException(final @NotNull RuntimeException cause) {
            super(TaskState.FAILED, cause);
        }

        @Override
        public boolean isDemandingAbort() { return true; }
    }

    protected static class FinishedException extends TransitException {
        private static final long serialVersionUID = 196914329532915066L;

        protected final Serializable result;

        protected FinishedException(final Serializable resultParam) {
            super(TaskState.FINISHED);
            result = resultParam;
        }
    }

    private static class StopException extends TransitException {
        private static final long serialVersionUID = 7783981039310846994L;


        protected StopException(final @NotNull TaskState newStateParam) {
            super(newStateParam);
        }

        @Override
        public boolean isDemandingAbort() { return true; }
    }

    public TaskAction() {
        super();
    }

    
    public TaskAction(final @NotNull EntityManager em, final @NotNull Task model) {
        super();
        initFromModel(em, model);
    }


    public void initFromModel(final EntityManager em, final Task model) {

        boolean wasActive = em.getTransaction().isActive();
        if (!wasActive)
            em.getTransaction().begin();
        try {
            final boolean contained = em.contains(model);
            if (! contained) {
                try {
                    em.persist(model);
                }
                catch (EntityExistsException e) {
                    em.merge(model);
                }
            }
            if (!wasActive)
                em.getTransaction().commit();
            setOwnEntityManager(em);
            setModel(model);
        }
        finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }


    public TaskAction(final @NotNull EntityManager em, final @NotNull String pk) {
        super();
        initFromPk(em, pk);
    }


    public void initFromPk(final EntityManager em, final String pk) {
        boolean wasActive = em.getTransaction().isActive();
        if (!wasActive) em.getTransaction().begin();
        try {
            final Task model = em.find(getTaskClass(), pk);
            if (model == null)
                throw new IllegalArgumentException("Model not found for pk: " + pk);
            if (!wasActive) em.getTransaction().commit();
            setOwnEntityManager(em);
            setModel(model);
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }


    @Override
    public void setModel(final @NotNull Task mdl) {
        super.setModel(mdl);    // Overridden method
    }



    protected abstract @NotNull Class<Task> getTaskClass();


    public TaskExecutionService getService() {
        if (service == null) {
            final TaskAction taskAction = nextParentOfType(TaskAction.class);
            return taskAction == null ? null : taskAction.getService();
        }
        return service;
    }


    public void setService(final @NotNull TaskExecutionService serviceParam) {
        if (service == null)
            service = serviceParam;
        else
           throw new IllegalStateException("Can't overwrite service");
    }


    @Override
    protected final boolean isExecutingInsideTransaction() {
        return false;
    }


    @SuppressWarnings({ "ThrowableInstanceNeverThrown", "ObjectAllocationInLoop" })
    @Override
    public Task execute(final @NotNull EntityManager em) {
        boolean first = true;
        TransitException curEx = null;
        do {
            try {
                try {
                    if (first) {
                        try {
                            transit(null);
                        }
                        finally {
                            first = false;                            
                        }
                    }
                    else
                        transit(curEx.newState);
                    // if we come here, transit has failed
                    throw new IllegalStateException("No proper TransitException thrown");
                }
                /* On stop: finish loop and return model to caller */
                catch (final StopException newEx) {
                    markAsDone();
                    curEx = null;
                }
                /* On transit: switch to next state according to newEx */
                catch (final TransitException newEx) {
                    curEx = newEx;
                }
                /* On shutdown: Do not set the task to failed, simply throw e */
                catch (final ShutdownTaskActionException e) {
                    throw e;
                }
                /* On runtime ex: Set task to failed */
                catch (RuntimeException e) {
                    /* Cant go to FAILED after FINISHED, i.e. onFinish must never fail */
                    if (TaskState.FINISHED.equals(getModel().getState()))
                        throw e;
                    else
                        fail(e);
                }
            }
            /* If fail was called due to a RuntimeException in above try block, use resulting
               exception for the next state transition.  Ensures that the failed state of the
               task will be persisted.
             */
            catch (TransitException newEx) {
                curEx = newEx;
            }
            refreshTaskResource();
        } while (curEx != null);
        return getModel();
    }


    protected void refreshTaskResource() {
        try {
            getPostponedActions().getListener().onModelChange(getModel());
        }
        catch (RuntimeException e) {
            // intentionally ignored
        }
    }


    private void markAsDone() {
        final @NotNull Task model = getModel();
        if (! model.isDone()) {
            final EntityManager em = getEntityManager();
            try {
                em.getTransaction().begin();
                Task newModel = em.find(Task.class, getModel().getId());
                newModel.setDone(true);
                em.getTransaction().commit();
                setModel(newModel);
            }
            catch (RuntimeException e) {
                throw e;
            }
            finally {
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }
        }
    }

    @SuppressWarnings({ "CaughtExceptionImmediatelyRethrown" })
    private void transit(final TaskState newState) {
        final EntityManager em = getEntityManager();
        try {
            @NotNull Task model = getModel();
            em.getTransaction().begin();
            if (newState != null) {
                if (! em.contains(model)) {
                    try {
                        try {
                            em.persist(model);
                        }
                        catch (EntityExistsException e) {
                            rewindTransaction(em.getTransaction());
                            em.merge(model);
                        }
                    }
                    catch (RuntimeException e2) {
                            rewindTransaction(em.getTransaction());
                            final @NotNull Task newModel = em.find(Task.class, model.getId());
                            model.stampOn(em, newModel);
                            setModel(newModel);
                            model = getModel();
                        }
                    }
                }
            model.transit(newState);
            em.getTransaction().commit();
            final TaskState modelState = model.getState();
            transit(modelState, model);
        }
        // for debugging
        catch (RuntimeException e) {
            throw e;
        }
        finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }


    private void rewindTransaction(final EntityTransaction txParam) {
        if (txParam.getRollbackOnly()) {
            txParam.rollback();
            txParam.begin();
        }
    }


    @SuppressWarnings({ "ThrowableInstanceNeverThrown" })
    private void transit(final @NotNull TaskState newState, final @NotNull Task model) {
        switch (model.getState().transit(newState)) {
            case CREATED: onCreated(model); break;
            case CREATED_UNKNOWN: onUnknown(model); break;
            case INITIALIZED: onInitialized(model); break;
            case INITIALIZED_UNKNOWN: onUnknown(model); break;
            case IN_PROGRESS: onInProgress(model); break;
            case IN_PROGRESS_UNKNOWN: onUnknown(model); break;
            case FAILED:
                    if (! model.isDone())
                        onFailed(model);
                    /* safety catch-all */
                    throw new StopException(TaskState.FAILED);
            case FINISHED:
                    if (! model.isDone())
                        onFailed(model);
                    onFinished(model);
                    /* safety catch-all */
                    throw new StopException(TaskState.FINISHED);
            default:
                throw new IllegalStateException("Invalid or unsupported task state");
        }
    }

    protected TaskState onUnknown(final Task model) {
        throw new UnsupportedOperationException();
    }


    protected void onCreated(final Task model) {
        transitToState(TaskState.INITIALIZED);

    }

    protected void onInitialized(final Task model) {
        transitToState(TaskState.IN_PROGRESS);
    }

    protected abstract void onInProgress(final @NotNull Task model);


    protected void onFailed(final @NotNull Task model) {
        stop(model);
    }


    protected void onFinished(final @NotNull Task model) {
        stop(model);
    }


    protected static void transitToState(final @NotNull TaskState newState) {
        throw new TransitException(newState);
    }


    protected void fail(final @NotNull RuntimeException e) {
        getModel().fail(e);
        throw new FailedException(e);
    }


    protected void finish(final Serializable result) {
        getModel().finish(result);
        throw new FinishedException(result);
    }


    protected void stop(final @NotNull Task model) {
        throw new StopException(model.getState());
    }

    
    protected final InterruptedException wrapInterrupt(InterruptedException e) {
        if (getService().isTerminating())
            throw new ShutdownTaskActionException(e);
        return e;
    }

    @SuppressWarnings({ "ThrowableResultOfMethodCallIgnored" })
    protected final void wrapInterrupt_(InterruptedException e) {
        wrapInterrupt(e);
    }


    public static boolean isTransitException( Exception e ) {
        return e instanceof TransitException;
    }
}
