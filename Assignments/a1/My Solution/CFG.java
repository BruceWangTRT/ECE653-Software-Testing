import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Stack;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;

public class CFG {
    Set<Node> nodes = new HashSet<Node>();
    Map<Node, Set<Node>> edges = new HashMap<Node, Set<Node>>();

    static class Node {
	int position;
	MethodNode method;
	ClassNode clazz;

	Node(int p, MethodNode m, ClassNode c) {
	    position = p; method = m; clazz = c;
	}

	public boolean equals(Object o) {
	    if (!(o instanceof Node)) return false;
	    Node n = (Node)o;
	    return (position == n.position) &&
		method.equals(n.method) && clazz.equals(n.clazz);
	}

	public int hashCode() {
	    return position + method.hashCode() + clazz.hashCode();
	}

	public String toString() {
	    return clazz.name + "." +
		method.name + method.signature + ": " + position;
	}
    }

    public void addNode(int p, MethodNode m, ClassNode c) {
	// ...
    	Node newnode = new Node(p,m,c);
    	if(nodes.contains(newnode)){
    		//no modification
    	}
    	else{
    		nodes.add(newnode);
    		edges.put(newnode, new HashSet<Node>());
    	}
    		
    }

    public void addEdge(int p1, MethodNode m1, ClassNode c1,
			int p2, MethodNode m2, ClassNode c2) {
    	Node edgeStartNode = new Node(p1,m1,c1);
    	Node edgeEndNode = new Node(p2,m2,c2);

    	//check if(nodes.contains(edgeStartNode)) is done in method addNode
    	addNode(p1,m1,c1);

    	//check if(nodes.contains(edgeEndNode)) is done in method addNode
    	addNode(p2,m2,c2);
		edges.get(edgeStartNode).add(edgeEndNode);
    	
	// ...
    }
	
	public void deleteNode(int p, MethodNode m, ClassNode c) {
		// ...
		Node toBeDeletedNode = new Node(p,m,c);
		if(nodes.contains(toBeDeletedNode)){
			nodes.remove(toBeDeletedNode);
			edges.remove(toBeDeletedNode);
			for(Set<Node> nodeSetX:edges.values()){
				if(nodeSetX.contains(toBeDeletedNode)){
					nodeSetX.remove(toBeDeletedNode);
				}
			}
		}
    }
	
    public void deleteEdge(int p1, MethodNode m1, ClassNode c1,
						int p2, MethodNode m2, ClassNode c2) {
		Node toBeDeletedStart = new Node(p1,m1,c1);
		Node toBeDeletedEnd = new Node(p2,m2,c2);
		if(edges.containsKey(toBeDeletedStart)){
			if(edges.get(toBeDeletedStart).contains(toBeDeletedEnd)){
				edges.get(toBeDeletedStart).remove(toBeDeletedEnd);
			}
		}
    }
	

    public boolean isReachable(int p1, MethodNode m1, ClassNode c1,
			       int p2, MethodNode m2, ClassNode c2) {
    	Node isReachableStart = new Node(p1,m1,c1);
    	Node isReachableEnd = new Node(p2,m2,c2);
    	if(nodes.contains(isReachableStart)){
    		if(nodes.contains(isReachableEnd)){
    			if(isReachableStart.equals(isReachableEnd)){   				
    				return true;
    			}
    			else{
    				//BFS
    				Set<Node> visited = new HashSet<Node>();
    				Stack<Node> nodeStack = new Stack<Node>();
    				visited.add(isReachableStart);
    				nodeStack.add(isReachableStart);
    				while(!nodeStack.isEmpty()){
    					Node a= nodeStack.pop();
    					for(Node nodeX:edges.get(a)){
    						if(visited.contains(nodeX)){
    							continue;
    						}
    						else if(nodeX.equals(isReachableEnd)){
    							return true;
    						}
    						else{
    							visited.add(nodeX);
    							nodeStack.add(nodeX);
    						}
    					}
    				}
    				return false;
    			}
    		}
    		else{
    			return false;
    		}
    	}
    	else{
    		return false;
    	}
    }
}
