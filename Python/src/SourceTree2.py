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
    ignore_type_for_equals_brute_two_nodes = (
    'lineno', 'col_offset', 'ctx', 'parent', 'brother', 'childs', 'all_node_tree')
    WILDCARDS_ANY = 'any'
    WILDCARDS_ANY_FUNCTION = 'anyFunction'
    WILDCARDS_ANY_VARIABLE = 'anyVariable'

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
                if tipe not in self.ignore_type_for_equals_brute_two_nodes:
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
        piter_child_default = list(piter_child)
        set_match = {}
        last_key_match = tuple()
        for node in node_child:
            for piter in piter_child:
                if self.brute_equals_two_nodes(node, piter):
                    source_lineno_found = vars(node).get("lineno")
                    source_coloffset_found = vars(node).get("col_offset")
                    last_key_match = (source_lineno_found, source_coloffset_found)
                    set_match[(source_lineno_found, source_coloffset_found)] = piter
                    piter_child.remove(piter)
                else:
                    break

            if len(piter_child) == 0:
                piter_child = list(piter_child_default)

        if len(set_match) % 2 == 1 and len(set_match) != 1:
            set_match.pop(last_key_match)
        if len(set_match) >= len_piter:
            return set_match
        return None

    def brute_equals_nodes_and_iter_child(self, node_child, iter_child):
        iter_child = list(iter_child)
        if len(node_child) is 0 or len(iter_child) is 0:
            return None
        if len(node_child) < len(iter_child):
            return None
        result = self.subset_nodechild_iterchild(node_child, iter_child)
        if result:
            return result

    def search_pattern_exact_equals(self, node, pattern):
        node_child = vars(node).get("childs")
        pattern_child = ast.iter_child_nodes(pattern)
        return self.brute_equals_nodes_and_iter_child(node_child, pattern_child)

    # HANDLE WILDCARDS FUNCTIONS

    def wildcard_assign_is_any(self, node):
        if isinstance(node, ast.Assign):
            str_node = vars(node).get("value")
            value_string = vars(str_node).get("s")
            if value_string == self.WILDCARDS_ANY:
                return True
        return False

    def wildcard_if_condition_is_any(self, node):
        if isinstance(node, ast.If):
            str_node = vars(node).get("test")
            value_string = vars(str_node).get("s")
            if value_string == self.WILDCARDS_ANY:
                return True
        return False

    def wildcard_if_body_is_any(self, node):
        if isinstance(node, ast.If):
            body_value = vars(node).get("body")
            if len(body_value) > 1:
                return False
            value_body = vars(body_value[0]).get("value")
            if isinstance(value_body, ast.Ellipsis):
                return True
        return False

    def __value_str(self, node):
        if isinstance(node, ast.Str):
            return vars(node).get("s")
        return ""

    def is_equals_ast_name(self, node1, node2):
        if isinstance(node1, ast.Name) and isinstance(node2, ast.Name):
            node1_id = vars(node1).get("id")
            node2_id = vars(node2).get("id")
            if node1_id == node2_id:
                return True

        return False

    def get_id_in_ast_name(self, node):
        if isinstance(node, ast.Name):
            node_id = vars(node).get("id")
            return node_id
        return ""

    def wildcard_call_is_any_params(self, node1, node2):
        if isinstance(node1, ast.Expr) and isinstance(node2, ast.Expr):
            args1 = vars(vars(node1).get("value")).get("args")
            args2 = vars(vars(node2).get("value")).get("args")

            if self.brute_equals_two_nodes(node1, node2):
                return True

            if not len(args1) is len(args2):
                return False

            for arg1, arg2 in zip(args1, args2):
                if self.__value_str(arg2) != self.WILDCARDS_ANY and not self.brute_equals_two_nodes(arg1, arg2):
                    return False

        return True

    def wild_validation_any_items(self, node1, node2):
        if type(node1) is type(node2):

            # value = 'any'
            if isinstance(node1, ast.Assign) and isinstance(node2, ast.Assign):
                if self.wildcard_assign_is_any(node2):
                    print(node1, node2)
                    return True

            # if 'any':
            #  ...
            if isinstance(node1, ast.If) and isinstance(node2, ast.If):
                if self.wildcard_if_condition_is_any(node2) and self.wildcard_if_body_is_any(node2):
                    print(node1, node2)
                    return True

            # function('any', 'any')
            if isinstance(node1, ast.Expr) and isinstance(node2, ast.Expr):

                func1 = vars(vars(node1).get("value")).get("func")
                func2 = vars(vars(node2).get("value")).get("func")

                if not self.WILDCARDS_ANY_FUNCTION in self.get_id_in_ast_name(func2):
                    return False

                if self.wildcard_call_is_any_params(node1, node2):
                    print(node1, node2)
                    return True

    def wild_validation_declare_itens(self, node1, node2):
        if type(node1) is type(node2):

            # anyFunction('any', 'any')
            if isinstance(node1, ast.Expr) and isinstance(node2, ast.Expr):

                func1 = vars(vars(node1).get("value")).get("func")
                func2 = vars(vars(node2).get("value")).get("func")

                if not self.is_equals_ast_name(func1, func2):
                    return False

                if self.wildcard_call_is_any_params(node1, node2):
                    return True

            # any = ...
            if isinstance(node1, ast.Assign) and isinstance(node2, ast.Assign):

                target1 = vars(node1).get("targets")[0]
                target2 = vars(node2).get("targets")[0]

                if self.WILDCARDS_ANY_VARIABLE in self.get_id_in_ast_name(target2):
                    if self.wildcard_assign_is_any(node2):
                        return True
                    value1 =  vars(node1).get("value")
                    value2 =  vars(node2).get("value")
                    if self.brute_equals_two_nodes(value1, value2):
                        return True

        return False

    def wildcards_equals_two_nodes(self, node1, node2):

        # anyvalidation: in value => DefineValue = 'any'

        if isinstance(node1, ast.AST) and isinstance(node2, ast.AST):

            if self.wild_validation_any_items(node1, node2):
                return True

            if self.wild_validation_declare_itens(node1, node2):
                return True

        # anyvalidation: in value => anyValue = 'any'

        # anyvalidation: in value => valor = 'any'

        # anyvalidation: in value => anyValue = 'anyNumber'

        # anyvalidation in value => anyNumber = 'anyLiteralValue'

        # anyvalidation in value => anyNumber = 'anyExpression'

        # anyCompare

        # anyItem

    def wildcards_subset_nodechild_iterchild(self, node_child, piter_child):
        len_piter = len(piter_child)
        piter_child_default = list(piter_child)
        set_match = {}
        last_key_match = tuple()
        for node in node_child:
            for piter in piter_child:
                if self.wildcards_equals_two_nodes(node, piter):
                    source_lineno_found = vars(node).get("lineno")
                    source_coloffset_found = vars(node).get("col_offset")
                    last_key_match = (source_lineno_found, source_coloffset_found)
                    set_match[(source_lineno_found, source_coloffset_found)] = piter
                    piter_child.remove(piter)
                else:
                    break

            if len(piter_child) == 0:
                piter_child = list(piter_child_default)

        if len(set_match) % 2 == 1 and len(set_match) != 1:
            set_match.pop(last_key_match)
        if len(set_match) >= len_piter:
            return set_match
        return None

    def wildcards_equals_nodes_and_iter_child(self, node_child, iter_child):
        iter_child = list(iter_child)
        if len(node_child) is 0 or len(iter_child) is 0:
            return None
        if len(node_child) < len(iter_child):
            return None
        result = self.wildcards_subset_nodechild_iterchild(node_child, iter_child)
        if result:
            return result

    def search_pattern_wildcards_equals(self, node, pattern):
        node_child = vars(node).get("childs")
        pattern_child = ast.iter_child_nodes(pattern)
        return self.wildcards_equals_nodes_and_iter_child(node_child, pattern_child)

    def search_pattern(self, pattern):
        source = self.root
        for node in ast.walk(source):
            found_exact_equals = self.search_pattern_exact_equals(node, pattern)
            if found_exact_equals:
                print(found_exact_equals)
            else:
                found_wildcards_equals = self.search_pattern_wildcards_equals(node, pattern)
                if found_wildcards_equals:
                    print(found_wildcards_equals)
                pass
