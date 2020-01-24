import ast
class SourceTree():
    root = None
    def __init__(self, root_tree):
        self.root = root_tree
        self.__handle_tree(self.root)

    def __handle_tree(self, root):
        new_root = self.__add_parent(root)
        self.root = new_root

    def __add_parent(self, tree):
        for node in ast.walk(tree):
            for child in ast.iter_child_nodes(node):
                child.parent = node
        return tree

    def is_equals(self, other_tree):
        my_root = self.root
        result = self.__equals_tree(my_root, other_tree)
        return result

    def __equals_tree(self, node1, node2):
        if type(node1) != type(node2):
            return False
        if(isinstance(node1, ast.AST)):
            for tipe, var in vars(node1).items():
                if tipe not in ('lineno', 'col_offset', 'ctx', 'parent'):
                    ctx_var = vars(node1).get('ctx')
                    if isinstance(ctx_var, ast.Store):
                        return True
                    if isinstance(node1, ast.Call) and tipe == 'args':
                        list_node1 = vars(node1).get(tipe)
                        list_node2 = vars(node2).get(tipe)
                        if len(list_node1) == len(list_node2):
                            return True
                    var2 = vars(node2).get(tipe)
                    if not self.__equals_tree(var, var2):
                        return False
            return True
        elif isinstance(node1, list):
            if len(node1) != len(node2):
                return False
            for i in range(len(node1)):
                if not self.__equals_tree(node1[i], node2[i]):
                    return False
            return True
        else:
            return node1 == node2

    def is_subtree(self, root_pattern):
        mytree = self.root
        return self.__is_subtree(mytree ,root_pattern)

    def __is_subtree(self, mytree, root_pattern):
        for node_my_tree in ast.walk(mytree):
            for node_pattern in ast.walk(root_pattern):
                result = self.__equals_tree(node_my_tree, node_pattern)
                if(result):
                    return True

        return False
