import ast
import equals
class Analyzer(ast.NodeVisitor):
    cont = 0
    cont2 =0
    error = []
    def status(self):
        list_subtree = list(ast.iter_child_nodes(self.sub_tree))
        status_value = self.__verify_error(self.error, list_subtree)
        print(status_value)
        if(status_value > 0):
            return True
        else:
            return False
    def __verify_error(self, error, subtree):
        error_node = []
        error_node_subtree = []
        for indexJ in range(len(error)):
            error_node.append(error[indexJ][0])
            error_node_subtree.append(error[indexJ][1])
        if len(subtree) == 1:
            return len(error)
        else:
            cont = sum(1 for indexI in range(len(error_node_subtree)) if error_node_subtree[indexI:indexI+len(subtree)] == subtree)
            #print(cont)
            return cont
    def detals_pattern(self, error):
        pass
    def length_subtree(self, subtree):        
        return len(vars(subtree).get('body'))
        
        
    def __init__(self, tree, sub_tree):  
        self.tree = tree
        self.sub_tree = sub_tree        
        ast.NodeVisitor.generic_visit(self, tree)

    
    def generic_visit(self, node):
        for node_subtree in ast.iter_child_nodes(self.sub_tree):
            value = equals.isEquals(node,node_subtree)
            if(value):
                lis_aux = []
                lis_aux.append(node)
                lis_aux.append(node_subtree)
                self.error.append(lis_aux)
                break
        ast.NodeVisitor.generic_visit(self, node)


