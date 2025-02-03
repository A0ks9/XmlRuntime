package com.flipkart.android.proteus.toolbox

import android.view.View
import com.flipkart.android.proteus.DataContext
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * Kotlin class that wraps a [ProteusView.Manager] instance, providing a base for extension or decoration.
 *
 * This class implements the [ProteusView.Manager] interface and delegates all method calls
 * to an underlying 'base' [ProteusView.Manager] instance. It serves as a wrapper, allowing you
 * to intercept or extend the behavior of an existing Proteus View Manager without modifying its original implementation.
 *
 * @property base The base [ProteusView.Manager] instance to which all calls are delegated.
 *               This is typically the actual manager that performs the core view management logic.
 * @constructor Creates a new [ManagerWrapper] instance wrapping the given [base] manager.
 * @param base The [ProteusView.Manager] instance to wrap. Must not be null.
 */
open class ManagerWrapper(private val base: ProteusView.Manager) :
    ProteusView.Manager { // Converted to Kotlin, made 'open' for potential inheritance

    /**
     * Updates the view managed by this wrapper with the provided [data].
     *
     * This method delegates the update call to the underlying [base] manager.
     *
     * @param data The [ObjectValue] containing the data to update the view with. May be null.
     */
    override fun update(data: ObjectValue?) { // Converted to Kotlin syntax, @Nullable is handled by '?'
        base.update(data) // Delegate the update call to the base manager
    }

    /**
     * Finds a view within the managed view hierarchy by its [id].
     *
     * This method delegates the view finding operation to the underlying [base] manager.
     *
     * @param id The string ID of the view to find. Must not be null.
     * @return The [View] with the given [id], or null if no view with that ID is found.
     */
    override fun findViewById(id: String): View? { // Converted to Kotlin syntax, @NonNull is handled by non-null type, @Nullable by '?'
        return base.findViewById(id) // Delegate findViewById call to the base manager
    }

    /**
     * Returns the [ProteusContext] associated with this manager.
     *
     * This method delegates the context retrieval to the underlying [base] manager.
     *
     * @return The [ProteusContext] of the base manager. Never returns null.
     */
    override val context: ProteusContext = base.context // Delegate context call to the base manager

    /**
     * Returns the [Layout] associated with this manager.
     *
     * This method delegates the layout retrieval to the underlying [base] manager.
     *
     * @return The [Layout] of the base manager. Never returns null.
     */
    override val layout: Layout = base.layout // Delegate layout call to the base manager

    /**
     * Returns the [DataContext] associated with this manager.
     *
     * This method delegates the data context retrieval to the underlying [base] manager.
     *
     * @return The [DataContext] of the base manager. Never returns null.
     */
    override val dataContext: DataContext =
        base.dataContext // Delegate dataContext call to the base manager

    /**
     * Returns any extra data associated with this manager.
     *
     * This method delegates the extras retrieval to the underlying [base] manager.
     *
     * @return The extras object, or null if no extras are set.
     */
    override val extras: Any? = base.extras // Delegate extras call to the base manager

    /**
     * Sets extra data to be associated with this manager.
     *
     * This method delegates the extras setting operation to the underlying [base] manager.
     *
     * @param extras The extra data to set. May be null.
     */
    override fun setExtras(extras: Any?) { // Converted to Kotlin syntax, @Nullable is handled by '?'
        base.setExtras(extras) // Delegate setExtras call to the base manager
    }

    /**
     * Returns the underlying base [ProteusView.Manager] instance.
     *
     * This method provides access to the wrapped base manager.
     *
     * @return The base [ProteusView.Manager] instance. Never returns null.
     */
    fun getBaseManager(): ProteusView.Manager { // Converted to Kotlin syntax, @NonNull is handled by non-null return type
        return base // Returns the base manager instance
    }
}