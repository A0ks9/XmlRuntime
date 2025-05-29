package com.voyager.data.models

import org.junit.Assert.*
import org.junit.Test

class AttributeTest {

    @Test
    fun `instantiation and property values are correct`() {
        val attributeName = "test:attributeName"
        val attributeReference = 12345

        val attribute = Attribute(name = attributeName, reference = attributeReference)

        assertEquals("Name should match the constructor argument", attributeName, attribute.name)
        assertEquals("Reference should match the constructor argument", attributeReference, attribute.reference)
    }

    @Test
    fun `data class equals and hashCode work as expected`() {
        val attr1 = Attribute("android:layout_width", 16842996) // android.R.attr.layout_width
        val attr2 = Attribute("android:layout_width", 16842996) // Same as attr1
        val attr3 = Attribute("android:layout_height", 16842997) // Different name, different ref
        val attr4 = Attribute("android:layout_width", 16842997) // Same name as attr1, different ref
        val attr5 = Attribute("android:textColor", 16842996)    // Different name, same ref as attr1

        // Test equals
        assertEquals("Instances with same name and reference should be equal", attr1, attr2)
        assertNotEquals("Instances with different names should not be equal", attr1, attr3)
        assertNotEquals("Instances with different references should not be equal", attr1, attr4)
        assertNotEquals("Instances with different names but same reference should not be equal", attr1, attr5)

        // Test hashCode
        assertEquals("HashCodes for equal instances should be the same", attr1.hashCode(), attr2.hashCode())
        // While not strictly guaranteed by contract, typically different objects will have different hashCodes
        // unless there's a collision. We expect data classes to generate them based on properties.
        assertNotEquals("HashCodes for instances with different names should ideally differ", attr1.hashCode(), attr3.hashCode())
        assertNotEquals("HashCodes for instances with different references should ideally differ", attr1.hashCode(), attr4.hashCode())
        assertNotEquals("HashCodes for instances with different names but same ref should ideally differ", attr1.hashCode(), attr5.hashCode())
    }

    @Test
    fun `data class toString contains property names and values`() {
        val attributeName = "app:customAttr"
        val attributeReference = 98765
        val attribute = Attribute(name = attributeName, reference = attributeReference)

        val attributeString = attribute.toString()

        assertTrue("toString should contain the name property", attributeString.contains("name=$attributeName"))
        assertTrue("toString should contain the reference property", attributeString.contains("reference=$attributeReference"))
        assertTrue("toString output for data class should generally start with class name", attributeString.startsWith("Attribute("))
    }

    @Test
    fun `copy method works as expected`() {
        val originalName = "original:name"
        val originalReference = 1001
        val originalAttribute = Attribute(name = originalName, reference = originalReference)

        // 1. Copy and change name
        val newName = "copied:newName"
        val copiedWithNameChanged = originalAttribute.copy(name = newName)

        assertEquals("Name should be updated in copied instance", newName, copiedWithNameChanged.name)
        assertEquals("Reference should remain unchanged in name-copied instance", originalReference, copiedWithNameChanged.reference)
        assertNotEquals("Copied instance should be different from original", originalAttribute, copiedWithNameChanged)

        // 2. Copy and change reference
        val newReference = 2002
        val copiedWithReferenceChanged = originalAttribute.copy(reference = newReference)

        assertEquals("Name should remain unchanged in reference-copied instance", originalName, copiedWithReferenceChanged.name)
        assertEquals("Reference should be updated in copied instance", newReference, copiedWithReferenceChanged.reference)
        assertNotEquals("Copied instance should be different from original", originalAttribute, copiedWithReferenceChanged)

        // 3. Copy and change both name and reference
        val anotherNewName = "another:name"
        val anotherNewReference = 3003
        val copiedWithBothChanged = originalAttribute.copy(name = anotherNewName, reference = anotherNewReference)

        assertEquals("Name should be updated", anotherNewName, copiedWithBothChanged.name)
        assertEquals("Reference should be updated", anotherNewReference, copiedWithBothChanged.reference)
        assertNotEquals("Copied instance should be different from original", originalAttribute, copiedWithBothChanged)


        // 4. Assert original instance remains unchanged
        assertEquals("Original name should not change after copy", originalName, originalAttribute.name)
        assertEquals("Original reference should not change after copy", originalReference, originalAttribute.reference)
    }
}
