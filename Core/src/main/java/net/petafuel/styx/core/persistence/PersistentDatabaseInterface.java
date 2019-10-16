package net.petafuel.styx.core.persistence;

/**
 * Interface for Styx persistence/database interaction
 *
 * <p>
 *     Classes that interact with the database through this interface should abide to the CRUD structure<br>
 *     create<br>
 *     get/read<br>
 *     update<br>
 *     delete<br>
 * </p>
 *
 * @param <T> Model Class to be saved and retrived from the Database
 */
public interface PersistentDatabaseInterface<T> {
    /**
     * Insert a model into the database
     * @param model Model to be saved in the database
     * @return Returns the saved model
     */
    T create(T model);

    /**
     * Retrieve a model from the database
     * @param model Model to get from the database e.g. model contains an id only on which the remaining values are selected
     * @return Returns a model loaded from the database
     */
    T get(T model);

    /**
     * Update a database entry related to the model parameter
     * @param model Database entry that should be updated
     * @return Returns the updated model
     */
    T update(T model);

    /**
     * Deletes a model from the database
     * @param model Model that should be deleted from the database
     * @return Returns the full model with data which was just deleted in the database
     */
    T delete(T model);
}
