package com.voyager.core.data.utils

import com.voyager.core.model.ViewNode
import java.util.Stack

/**
 * Implementation of [XmlTokenStream] that builds a [ViewNode] hierarchy
 * from the streamed XML tokens.
 */
class ViewNodeTokenStream : XmlTokenStream {
    private val nodeStack = Stack<ViewNode>()
    private var rootNode: ViewNode? = null
    private var sha256Hash: ByteArray? = null

    override fun onToken(token: XmlToken) {
        when (token) {
            is XmlToken.StartElement -> {
                val node = ViewNode(
                    type = token.type, attributes = token.attributes, children = mutableListOf()
                )

                if (nodeStack.isEmpty()) {
                    rootNode = node
                } else {
                    nodeStack.peek().children.add(node)
                }

                nodeStack.push(node)
            }

            is XmlToken.EndElement -> {
                if (nodeStack.isNotEmpty()) {
                    nodeStack.pop()
                }
            }

            is XmlToken.Text -> {
                // Handle text content if needed
            }

            XmlToken.EndDocument -> {
                // Document parsing complete
            }
        }
    }

    override fun onComplete(sha256Hash: ByteArray) {
        this.sha256Hash = sha256Hash
    }

    /**
     * Get the parsed ViewNode and its SHA256 hash.
     * @return A [ParseResult] containing the parsed ViewNode and its hash
     */
    fun getResult(): ParseResult? {
        val node = rootNode ?: return null
        val hash = sha256Hash ?: return null
        return ParseResult(node, hash)
    }
} 