import ast
import tokenize
import inspect
import textwrap
import io
from collections import defaultdict
from src.gui.gui import *
from src.utils.tree_utils import *


class SourceTree():
    root = ast.AST()
    ignore_type_for_equals_two_nodes = ('lineno', 'col_offset', 'ctx', 'parent', 'brother', 'childs', 'all_node_tree')

    def __init__(self, root_tree):
        self.root = root_tree
        self.__handle_properties()

    def __handle_properties(self):
        tree = self.root
        tree = self.__add_parent_properties_in_node(tree)
        tree = self.__add_brother_properties_in_node(tree)
        tree = self.__add_childs_properties_in_node(tree)
        tree = self.__add_root_properties_in_node(tree)
        self.root = tree

    def __add_root_properties_in_node(self, tree):
        all_node_tree = list(ast.walk(tree))
        for node in ast.walk(tree):
            node.all_node_tree = all_node_tree
        return tree

    def __add_parent_properties_in_node(self, tree):
        """Adicionar pai em cada NODE da arvore.

        Arguments:
            tree {AST} -- codigo fonte pertecente ao objeto Source Tree.

        Returns:
            AST -- Arvore com o atributo parent adicionado em cada node.
        """
        for node in ast.walk(tree):
            for child in ast.iter_child_nodes(node):
                child.parent = node
        return tree

    def __add_brother_properties_in_node(self, tree):
        for node in ast.walk(tree):
            node.brother = []
            for node2 in ast.walk(tree):
                if not isinstance(node, ast.Module) and not isinstance(node2, ast.Module):
                    if node.parent == node2.parent:
                        node.brother.append(node2)
        return tree

    def __add_childs_properties_in_node(self, tree):
        for node in ast.walk(tree):
            node.childs = list(ast.iter_child_nodes(node))
        return tree

    def brute_equals_two_nodes(self, node1, node2):
        if type(node1) != type(node2):
            return False
        if isinstance(node1, ast.AST):
            for tipe, var in vars(node1).items():
                if tipe not in self.ignore_type_for_equals_two_nodes:
                    var2 = vars(node2).get(tipe)
                    if not self.brute_equals_two_nodes(var, var2):
                        return False
            return True
        elif isinstance(node1, list):
            if len(node1) != len(node2):
                return False
            for i in range(len(node1)):
                if not self.brute_equals_two_nodes(node1[i], node2[i]):
                    return False
            return True
        else:
            return node1 == node2

    def subset_nodechild_iterchild(self, node_child, piter_child):
        len_piter = len(piter_child)
        set_match = {}
        for node in node_child:
            for piter in piter_child:
                if self.brute_equals_two_nodes(node, piter):
                    set_match[piter] = node

        if len(set_match) == len_piter:
            return set_match
        return None

    def brute_equals_nodes_and_iter_child(self, node_child, iter_child):
        iter_child = list(iter_child)
        if len(node_child) is 0 or len(iter_child) is 0:
            return
        if len(node_child) < len(iter_child):
            return
        result = self.subset_nodechild_iterchild(node_child, iter_child)
        if result:
            print(result, iter_child)

    def search_pattern(self, pattern):
        source = self.root
        for node in ast.walk(source):
            node_child = vars(node).get("childs")
            pattern_child = ast.iter_child_nodes(pattern)
            self.brute_equals_nodes_and_iter_child(node_child, pattern_child)
