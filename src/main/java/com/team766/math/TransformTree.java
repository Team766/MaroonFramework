package com.team766.math;

import java.util.ArrayList;
import java.util.Hashtable;

public class TransformTree {
	private ArrayList<TransformTree> tree;

	private final String name;
	private TransformTree parent;
	private IsometricTransform transform;

	public TransformTree(final String rootName) {
		this.name = rootName;
	}

	private TransformTree(final String nameParam, final TransformTree parentParam) {
		this.name = nameParam;
		this.parent = parentParam;
	}

	public TransformTree addSubordinateTransform(final String nameParam) {
		TransformTree subtree = new TransformTree(nameParam, this);
		tree.add(subtree);
		return subtree;
	}

	public void setLocalTransform(final IsometricTransform xf) {
		transform = xf;
	}

	public IsometricTransform getLocalTransform() {
		return transform;
	}

	public IsometricTransform getTransformRelativeTo(final String nameParam) {
		TransformTree other = getRoot().findTransform(name);
		if (other == null) {
			throw new IllegalArgumentException("Can't find a transform named " + nameParam);
		}
		return getTransformRelativeTo(other);
	}

	public IsometricTransform getTransformRelativeTo(final TransformTree other) {
		Hashtable<String, IsometricTransform> parentChain =
				new Hashtable<String, IsometricTransform>();
		IsometricTransform xf = transform;
		parentChain.put(name, xf);
		TransformTree iterator = this.parent;
		while (iterator != null) {
			xf = iterator.transform.compose(xf);
			parentChain.put(iterator.name, xf);
			iterator = iterator.parent;
		}
		iterator = other;
		xf = other.transform;
		while (iterator != null) {
			IsometricTransform first = parentChain.get(iterator.name);
			if (first != null) {
				return first.compose(xf.invert());
			}
		}
		throw new IllegalArgumentException("Transforms aren't part of the same tree");
	}

	public TransformTree findTransform(final String nameParam) {
		if (this.name == nameParam) {
			return this;
		}
		for (TransformTree sub : tree) {
			TransformTree subResult = sub.findTransform(nameParam);
			if (subResult != null) {
				return subResult;
			}
		}
		return null;
	}

	private TransformTree getRoot() {
		TransformTree iterator = this;
		while (iterator.parent != null) {
			iterator = iterator.parent;
		}
		return iterator;
	}
}
