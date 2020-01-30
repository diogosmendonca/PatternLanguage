from src.gui.gui import *

class SourceTree():
    root = None
    __occurrences = []

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

    def draw(self, subtree):
        draw_gui(self.root, subtree)

    def is_equals(self, other_tree):
        my_root = self.root
        result = self.__equals_tree(my_root, other_tree)
        return result

    def __equals_tree(self, node1, node2):
        if type(node1) != type(node2):
            return False
        if (isinstance(node1, ast.AST)):
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
        founded_tree = self.__find_subtree(mytree, root_pattern)
        if founded_tree and self.amount_of_patterns_found(root_pattern) > 0:
            return True
        else:
            return False


    def __find_subtree(self, mytree, root_pattern):
        for node_my_tree in ast.walk(mytree):
            for node_pattern in ast.walk(root_pattern):
                result = self.__equals_tree(node_my_tree, node_pattern)
                if (result):
                    return True
        return False

    def __walking_all_occurrences(self, root_mytree, root_pattern):
        occurrences = []
        for node_my_tree in ast.walk(root_mytree):
            for node_pattern in ast.iter_child_nodes(root_pattern):
                result = self.__equals_tree(node_my_tree, node_pattern)
                if (result):
                    lis_aux = []
                    lis_aux.append(node_my_tree)
                    lis_aux.append(node_pattern)
                    occurrences.append(lis_aux)
                    break
        return occurrences

    def __len_occurrences(self, error, root_pattern):
        root_pattern = list(ast.iter_child_nodes(root_pattern))
        error_node = []
        error_node_subtree = []
        for indexJ in range(len(error)):
            error_node.append(error[indexJ][0])
            error_node_subtree.append(error[indexJ][1])

        if len(root_pattern) == 1:
            return len(error)
        else:
            cont = sum(1 for indexI in range(len(error_node_subtree))
                       if error_node_subtree[indexI:indexI + len(root_pattern)] == root_pattern)
            return cont

    def amount_of_patterns_found(self, root_pattern):
        mytree = self.root
        occurrences_no_handle = self.__walking_all_occurrences(mytree, root_pattern)
        status_value = self.__len_occurrences(occurrences_no_handle, root_pattern)
        return status_value

    def get_all_occurrences(self, root_pattern):
        mytree = self.root
        occurrences_no_handle = self.__walking_all_occurrences(mytree, root_pattern)
        root_pattern = list(ast.iter_child_nodes(root_pattern))
        error_node = []
        error_node_subtree = []
        for indexJ in range(len(occurrences_no_handle)):
            error_node.append(occurrences_no_handle[indexJ][0])
            error_node_subtree.append(occurrences_no_handle[indexJ][1])

        occurrences = []

        for indexI in range(len(error_node_subtree)):
            if error_node_subtree[indexI:indexI + len(root_pattern)] == root_pattern:
                node_found = error_node[indexI:indexI + len(root_pattern)]
                occurrences.append(node_found)

        return occurrences

    def prettier_occurrences(self, root_pattern):
        occurrences = self.get_all_occurrences(root_pattern)
        for occurr in occurrences:
            for node_occur in occurr:
                print(vars(node_occur))
            print("-------------------")


