import ast
import equals

class Analyzer(ast.NodeVisitor):
    __error = []
    errors = []

    def status(self):
        list_subtree = list(ast.iter_child_nodes(self.sub_tree))
        status_value = self.__verify_error(self.__error, list_subtree)
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
        self.errors = error_node
        if len(subtree) == 1:
            return len(error)
        else:
            cont = sum(1 for indexI in range(len(error_node_subtree))
                       if error_node_subtree[indexI:indexI+len(subtree)] == subtree)
            return cont

    def detals_erro(self, errors=None, error=None):
        if errors == None and error == None:
            return None
        elif errors == None:
            print(error)
            error_dict = vars(error)
            return error_dict
        else:
            return [vars(indexI) for indexI in errors]

    def length_subtree(self, subtree):
        return len(vars(subtree).get('body'))

    def __init__(self, tree, sub_tree):
        self.tree = tree
        self.sub_tree = sub_tree
        ast.NodeVisitor.generic_visit(self, tree)

    def generic_visit(self, node):
        for node_subtree in ast.iter_child_nodes(self.sub_tree):
            value = equals.isEquals(node, node_subtree)
            if(value):
                lis_aux = []
                lis_aux.append(node)
                lis_aux.append(node_subtree)
                self.__error.append(lis_aux)
                break
        ast.NodeVisitor.generic_visit(self, node)
