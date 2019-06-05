import ast
import equals
class Analyzer(ast.NodeVisitor):
    status = False
    cont = 0
    cont2 =0
    def length_subtree(self, subtree):
        
        return len(vars(subtree).get('body'))
        
        
    def __init__(self, tree, sub_tree):  
        print(sub_tree)      
        print(vars(sub_tree))
        self.tree = tree
        self.sub_tree = sub_tree        
        ast.NodeVisitor.generic_visit(self, tree)        
    
    def generic_visit(self, node):
        for node_subtree in ast.iter_child_nodes(self.sub_tree):
            value = equals.isEquals(node,node_subtree)
            if(value):
                self.cont2 += 1
                break        
        if(self.cont2 == self.length_subtree(self.sub_tree)):
            self.status = True
        ast.NodeVisitor.generic_visit(self, node)


