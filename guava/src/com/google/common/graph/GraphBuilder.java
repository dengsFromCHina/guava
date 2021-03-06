/*
 * Copyright (C) 2016 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;

/**
 * A builder for constructing instances of {@link MutableGraph} with user-defined properties.
 *
 * <p>A graph built by this class will have the following properties by default:
 *
 * <ul>
 * <li>allows self-loops
 * <li>orders {@link Graph#nodes()} in the order in which the elements were added
 * </ul>
 *
 * <p>Example of use:
 *
 * <pre><code>
 * MutableGraph<String, Double> graph = GraphBuilder.undirected().build();
 * graph.putEdgeValue("Miami", "Denver", 5280.0);
 * </code></pre>
 *
 * @author James Sexton
 * @author Joshua O'Madadhain
 * @since 20.0
 */
@Beta
public final class GraphBuilder<N, V> extends AbstractGraphBuilder<N> {

  /** Creates a new instance with the specified edge directionality. */
  private GraphBuilder(boolean directed) {
    super(directed);
  }

  /** Returns a {@link GraphBuilder} for building directed graphs. */
  public static GraphBuilder<Object, Object> directed() {
    return new GraphBuilder<Object, Object>(true);
  }

  /** Returns a {@link GraphBuilder} for building undirected graphs. */
  public static GraphBuilder<Object, Object> undirected() {
    return new GraphBuilder<Object, Object>(false);
  }

  /**
   * Returns a {@link GraphBuilder} initialized with all properties queryable from {@code graph}.
   *
   * <p>The "queryable" properties are those that are exposed through the {@link Graph} interface,
   * such as {@link Graph#isDirected()}. Other properties, such as {@link #expectedNodeCount(int)},
   * are not set in the new builder.
   */
  public static <N> GraphBuilder<N, Object> from(Graph<N, ?> graph) {
    checkNotNull(graph);
    return new GraphBuilder<N, Object>(graph.isDirected())
        .allowsSelfLoops(graph.allowsSelfLoops())
        .nodeOrder(graph.nodeOrder());
  }

  /**
   * Specifies whether the graph will allow self-loops (edges that connect a node to itself).
   * Attempting to add a self-loop to a graph that does not allow them will throw an {@link
   * UnsupportedOperationException}.
   */
  public GraphBuilder<N, V> allowsSelfLoops(boolean allowsSelfLoops) {
    this.allowsSelfLoops = allowsSelfLoops;
    return this;
  }

  /**
   * Specifies the expected number of nodes in the graph.
   *
   * @throws IllegalArgumentException if {@code expectedNodeCount} is negative
   */
  public GraphBuilder<N, V> expectedNodeCount(int expectedNodeCount) {
    checkArgument(
        expectedNodeCount >= 0,
        "The expected number of nodes can't be negative: %s",
        expectedNodeCount);
    this.expectedNodeCount = Optional.of(expectedNodeCount);
    return this;
  }

  /** Specifies the order of iteration for the elements of {@link Graph#nodes()}. */
  public <N1 extends N> GraphBuilder<N1, V> nodeOrder(ElementOrder<N1> nodeOrder) {
    checkNotNull(nodeOrder);
    GraphBuilder<N1, V> newBuilder = cast();
    newBuilder.nodeOrder = nodeOrder;
    return newBuilder;
  }

  /** Returns an empty {@link MutableGraph} with the properties of this {@link GraphBuilder}. */
  public <N1 extends N, V1 extends V> MutableGraph<N1, V1> build() {
    return new ConfigurableMutableGraph<N1, V1>(this);
  }

  @SuppressWarnings("unchecked")
  private <N1 extends N, V1 extends V> GraphBuilder<N1, V1> cast() {
    return (GraphBuilder<N1, V1>) this;
  }
}
