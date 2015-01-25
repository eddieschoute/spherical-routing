package graph

import graph.Units.{Label, Node}

import scalax.collection.immutable.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._

object SphereApproximation {

	def approximateSphere(k: Int) = {
		val ico = Units.icosahedron
	}

	def subdivide(g: Graph[Node, UnDiEdge]) = {
		// Each set represents a triangle and because it is a set of sets, any duplicate sets are filtered out
		val tri = Util.triangles(g)

		// Add a unique label to each edge.
		val edgeLabels = (for((edge, index) ← g.edges.zipWithIndex) yield {
			val parentsLevel = edge.nodes.head.level
			edge.toOuter → Label(parentsLevel + 1, index)
		}).toMap
		println(s"Edge labels: $edgeLabels")

		val edges = g.edges.flatMap { edge ⇒
			println(edge)
			val currentLabel = edgeLabels(edge.toOuter)
			// Collect the two triangles that have at least two nodes in common with the edge.
			val relevantTriangles = tri.filter { triangle ⇒ edge.nodes.toSet.subsetOf(triangle)}
			println(relevantTriangles)

			// Find the labels of the edges between the triangle nodes
			val toLabels = relevantTriangles.flatMap { relevantTriangle ⇒
				// The subgraph spanning the triangle without the current edge
				val relevantGraph = g.filter(g.having(node = relevantTriangle.contains(_))) - edge
				println(s"Relevant graph $relevantGraph")
				relevantGraph.edges.map { rEdge ⇒ edgeLabels(rEdge.toOuter) }
			}
			val parentLabels = edge.nodes.toOuterNodes.toSet
			val allLabels = toLabels ++ parentLabels
			println(s"Make edges to labels $allLabels")

			// Make an edge to every label
			for(label ← allLabels) yield {
				currentLabel ~ label
			}
		}
		Graph.from[Node, UnDiEdge](edges = edges)
	}
}
