package com.voyager.data.models

import android.os.Parcel
import androidx.collection.ArrayMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.OLDEST_SDK]) // Basic Robolectric config for Parcel tests
class ViewNodeTest {

    private fun createSampleViewNode(
        id: String? = "node1",
        type: String = "TextView",
        activityName: String = "MainActivity",
        attributes: ArrayMap<String, String>? = null,
        children: List<ViewNode> = emptyList()
    ): ViewNode {
        val attrs = attributes ?: ArrayMap<String, String>().apply {
            put("text", "Hello")
            put("textSize", "16sp")
        }
        return ViewNode(
            id = id,
            type = type,
            activityName = activityName,
            attributes = attrs,
            children = children
        )
    }

    private fun parcelAndUnparcel(originalNode: ViewNode): ViewNode {
        val parcel = Parcel.obtain()
        originalNode.writeToParcel(parcel, originalNode.describeContents())
        parcel.setDataPosition(0) // Rewind parcel for reading
        val createdFromParcel = ViewNode.CREATOR.createFromParcel(parcel)
        parcel.recycle()
        return createdFromParcel
    }

    private fun assertArrayMapEquals(expected: ArrayMap<String, String>, actual: ArrayMap<String, String>) {
        assertEquals("ArrayMap sizes differ", expected.size, actual.size)
        for (i in 0 until expected.size) {
            val key = expected.keyAt(i)
            assertEquals("Value for key '$key' differs", expected.valueAt(i), actual[key])
        }
        // Note: ArrayMap iteration order is defined, so direct keyAt/valueAt comparison is valid.
        // For a more general map, one might sort keys or check map.entries.
    }

    @Test
    fun `parcelable test for simple ViewNode`() {
        val originalNode = createSampleViewNode()
        val unparceledNode = parcelAndUnparcel(originalNode)

        assertEquals(originalNode.id, unparceledNode.id)
        assertEquals(originalNode.type, unparceledNode.type)
        assertEquals(originalNode.activityName, unparceledNode.activityName)
        assertArrayMapEquals(originalNode.attributes, unparceledNode.attributes)
        assertEquals("Children list should be empty", 0, unparceledNode.children.size)
        assertEquals("Original and unparceled ViewNode should be equal", originalNode, unparceledNode)
    }

    @Test
    fun `parcelable test for ViewNode with children`() {
        val childAttrs = ArrayMap<String, String>().apply { put("child_attr", "child_value") }
        val childNode1 = createSampleViewNode("child1", "ImageView", "ChildActivity1", childAttrs)
        val childNode2 = createSampleViewNode("child2", "Button", "ChildActivity2", ArrayMap())

        val parentAttrs = ArrayMap<String, String>().apply { put("parent_attr", "parent_value") }
        val originalParentNode = createSampleViewNode(
            id = "parent1",
            type = "LinearLayout",
            activityName = "ParentActivity",
            attributes = parentAttrs,
            children = listOf(childNode1, childNode2)
        )

        val unparceledParentNode = parcelAndUnparcel(originalParentNode)

        assertEquals(originalParentNode.id, unparceledParentNode.id)
        assertEquals(originalParentNode.type, unparceledParentNode.type)
        assertEquals(originalParentNode.activityName, unparceledParentNode.activityName)
        assertArrayMapEquals(originalParentNode.attributes, unparceledParentNode.attributes)

        assertNotNull("Children list should not be null", unparceledParentNode.children)
        assertEquals("Number of children differs", originalParentNode.children.size, unparceledParentNode.children.size)

        // Assert equality of children (relies on ViewNode data class `equals` implementation)
        for (i in originalParentNode.children.indices) {
            assertEquals(
                "Child node at index $i differs",
                originalParentNode.children[i],
                unparceledParentNode.children[i]
            )
            // Deeper check for attributes of children
            assertArrayMapEquals(
                originalParentNode.children[i].attributes,
                unparceledParentNode.children[i].attributes
            )
        }
        assertEquals("Original and unparceled parent ViewNode should be equal", originalParentNode, unparceledParentNode)
    }

    @Test
    fun `parcelable test with empty children list`() {
        val originalNode = createSampleViewNode(children = emptyList())
        val unparceledNode = parcelAndUnparcel(originalNode)

        assertNotNull("Children list should not be null after unparceling", unparceledNode.children)
        assertEquals("Children list should be empty", 0, unparceledNode.children.size)
        assertEquals(originalNode, unparceledNode)
    }

    @Test
    fun `parcelable test for attributes map`() {
        val attributes = ArrayMap<String, String>().apply {
            put("attr1", "value1")
            put("attr2", "value2")
            put("attr3", "value3")
            put("android:layout_width", "match_parent")
        }
        val originalNode = createSampleViewNode(attributes = attributes)
        val unparceledNode = parcelAndUnparcel(originalNode)

        assertNotNull("Attributes map should not be null", unparceledNode.attributes)
        assertArrayMapEquals(originalNode.attributes, unparceledNode.attributes)
        assertEquals(originalNode, unparceledNode)
    }

    @Test
    fun `parcelable test with null id`() {
        val originalNode = createSampleViewNode(id = null)
        val unparceledNode = parcelAndUnparcel(originalNode)

        assertEquals(null, unparceledNode.id)
        assertEquals(originalNode, unparceledNode)
    }
}
