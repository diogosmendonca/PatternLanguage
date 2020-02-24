import ast
from src.gui.gui import *
from src.utils.tree_utils import *
from src.utils.data_utils import *

class SourceTree():
    """ Classe utilizada para a representação do código fonte """
    root = None
    __occurrences = []
    __variable_dict_wildcards = {}
    WILDCARD_ANY_LITERAL_VALUE = "anyLiteralValue"
    WILDCARD_ANY_FUNCTION = "anyFunction"
    WILDCARD_ANY_NUMBER = "anyNumber"
    WILDCARD_ANY_ITEM = "any"
    WILDCARD_ANY_NAME = "any"

    def __init__(self, root_tree):
        """Geracao de objeto Source Tree

        Arguments:
            root_tree {AST} -- Codigo fonte convertido por ast.parse
        """
        self.root = root_tree
        self.__handle_tree(self.root)

    def __handle_tree(self, root):
        """Tratar processos inicias para o funcionamento da arvore

        Arguments:
            root {AST} -- Codigo fonte pertecente ao objeto Source Tree.
        """
        new_root = self.__add_parent(root)
        self.root = new_root

    def __add_parent(self, tree):
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

    def draw(self, subtree):
        """Mostrar Represetacao grafica do objeto e a subarvore.

        Arguments:
            subtree {AST} -- Codigo da a subtree a ser mostrada
        """
        draw_gui(self.root, subtree)

    def is_equals(self, other_tree):
        """Verificar igualdade entre o objeto e arvore passada

        Arguments:
            other_tree {Ast} --  Codigo da subtree a ser verificada

        Returns:
            [Boolean] -- Respostas se arvores sao iguais
        """
        my_root = self.root
        result = self.__equals_tree_internal(my_root, other_tree)
        return result

    def __wildcards_validate_some_value(self, node1, node2):
        print("SOME VALUE VALIDATE", node1, node2)

    def __type_targets(self, targets1, targets2):
        return self.__equals_tree_internal(targets1, targets2)

    def __value_str(self, node):
        if isinstance(node, ast.Str):
            return vars(node).get("s")
        return ""

    def __wildcards_validate_assign(self, node1, node2):
        node1_targets = vars(node1).get('targets')
        node2_targets = vars(node2).get('targets')

        node1_value = vars(node1).get('value')
        node2_value = vars(node2).get('value')

        if not self.__type_targets(node1_targets, node2_targets):
            return False
        if isinstance(node1_value, ast.Call) and isinstance(node2_value, ast.Call):
            args1 = vars(node1_value).get('args')
            args2 = vars(node2_value).get('args')
            if len(args1) != len(args2):
                return False
            result = []
            for t1, t2 in zip(args1, args2):
                if not self.__equals_tree_internal(t1, t2):
                    return False
            return True

        if isinstance(node1_value, ast.AST) and isinstance(node2_value, ast.Str):
            if self.WILDCARD_ANY_ITEM == self.__value_str(node2_value):
                return True

        if isinstance(node1_value, ast.Num) and isinstance(node2_value, ast.Str):
            if self.WILDCARD_ANY_NUMBER in self.__value_str(node2_value):
                return True

        if isinstance(node1_value, ast.Str) and isinstance(node2_value, ast.Str):
            if self.WILDCARD_ANY_LITERAL_VALUE in self.__value_str(node2_value):
                return True

        return False

    def __wildcards_validate_any_name(self, node1, node2):
        name_targets1 = vars(node2).get('id')
        name_targets2 = vars(node1).get('id')
        result = self.WILDCARD_ANY_NAME in name_targets1
        if result:
            return True
        if name_targets1 == name_targets2:
            return True
        return False

    def __validate_wildcards(self, source_node, pattern_node):
        #validate name
        if isinstance(source_node, ast.Name) and isinstance(pattern_node, ast.Name):
            return self.__wildcards_validate_any_name(source_node, pattern_node)

        #validate assign
        if isinstance(source_node, ast.Assign) and isinstance(pattern_node, ast.Assign):
            return self.__wildcards_validate_assign(source_node, pattern_node)


        return False

    def __wildcards_validate_str(self, source_node, pattern_node):
        if isinstance(pattern_node, ast.Str) and isinstance(source_node, ast.AST):
            if self.WILDCARD_ANY_ITEM == self.__value_str(pattern_node):
                return True

        if isinstance(pattern_node, ast.Str) and isinstance(source_node, ast.Str):
            if self.WILDCARD_ANY_LITERAL_VALUE in self.__value_str(pattern_node):
                return True

        if isinstance(pattern_node, ast.Str) and isinstance(source_node, ast.Num):
            if self.WILDCARD_ANY_NUMBER in self.__value_str(pattern_node):
                return True

    def __equals_tree_internal(self, node1, node2):
        """Verificar igualdade entre dois "nodes raiz"

        Arguments:
            node1 {AST} -- node1 a ser comparado
            node2 {AST} -- node2: node a ser comparado

        Returns:
            Boolean -- Respostas se arvores sao iguais
        """
        if isinstance(node1, ast.AST) and isinstance(node2, ast.Str):
            if self.__wildcards_validate_str(node1, node2):
                return True

        return self.__equals_tree(node1, node2)



    def __equals_tree(self, node1, node2):
        if type(node1) != type(node2):
            return False
        if isinstance(node1, ast.AST):
            if self.__validate_wildcards(node1, node2):
                return True
            for tipe, var in vars(node1).items():
                if tipe not in ('lineno', 'col_offset', 'ctx', 'parent',):
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
        """Verificar se objeto possui o padrao passado como parametro como subarvore

         Arguments:
             node1 {AST} -- node1 a ser comparado
             node2 {AST} -- node2: node a ser comparado

         Returns:
             Boolean -- Respostas se root_pattern é subavore do Objeto
         """
        mytree = self.root
        founded_tree = self.__find_subtree(mytree, root_pattern)
        if founded_tree and self.amount_of_patterns_found(root_pattern) > 0:
            return True
        else:
            return False

    def __find_subtree(self, mytree, root_pattern):
        """ Informar se arvore(mytree) possui o determinado padrao (root_pattern)

            entrada: mytree: codigo da arvore a ser verificada
                     root_pattern: Codigo do padrao a ser verificado
        """
        for node_my_tree in ast.walk(mytree):
            for node_pattern in ast.walk(root_pattern):
                result = self.__equals_tree(node_my_tree, node_pattern)
                if (result):
                    return True
        return False


    def __walking_all_occurrences(self, root_mytree, root_pattern):
        """Percorrer todas as ocorrencias de um padrao(root_pattern) em uma arvore(root_mytree), 
        encontrando todos os nodes possiveis. OBS: informando ate os padroes parciais, isto é
        padroes nos quais uma instancia foi encontrada, independente se o bloco de intruncao foi contemplado por completo.

        Arguments:
            root_mytree {AST} -- Node raiz da arvore que sera processada
            root_pattern {AST} -- Node raiz do padrao do padrao procurado
        Returns:
            Array -- Todas as ocorrencias encontradas do padrao
        """
        occurrences = []
        childPattern = list(ast.iter_child_nodes(root_pattern))
        for node_my_tree in ast.walk(root_mytree):

            for node_pattern in (childPattern):
                result = self.__equals_tree(node_my_tree, node_pattern)
                if result :
                    lis_aux = []
                    lis_aux.append(node_my_tree)
                    lis_aux.append(node_pattern)
                    occurrences.append(lis_aux)
                    childPattern.remove(node_pattern)

            if len(childPattern) == 0:
                childPattern.extend(ast.iter_child_nodes(root_pattern))



        print(occurrences)
        return occurrences

    def __len_occurrences(self, error, root_pattern):

        root_pattern = list(ast.iter_child_nodes(root_pattern))
        error_node = []
        error_node_subtree = []
        for indexJ in range(len(error)):
            error_node.append(error[indexJ][0])
            error_node_subtree.append(error[indexJ][1])

        cont = 0
        if len(root_pattern) == 1:
            return len(error)
        else:
            for indexI in range(len(error_node_subtree)):
                val = error_node_subtree[indexI:indexI + len(root_pattern)]
                if val == root_pattern:
                    cont += 1
        return cont

    def __handle_occurrences(self, occurrences, root_pattern):
        """Filtrar ocorrencias não tratadas retornando os nodes

                Arguments:
                    error {Array} -- Lista de ocorrencias não tratadas

                Returns:
                    Array -- Ocorrencias.
                """
        root_pattern = list(ast.iter_child_nodes(root_pattern))
        occurrences_nodes = []
        occurrences_nodes_subtree = []
        for indexJ in range(len(occurrences)):
            occurrences_nodes.append(occurrences[indexJ][0])
            occurrences_nodes_subtree.append(occurrences[indexJ][1])
        occurrences_final = []


        for indexI in range(len(occurrences_nodes_subtree)):
            occurrences_found_subtree = occurrences_nodes_subtree[indexI:indexI + len(root_pattern)]
            occurrences_found_source = occurrences_nodes[indexI:indexI + len(root_pattern)]
            if occurrences_found_subtree == root_pattern:
                occurrences_final.append({'node_source': occurrences_found_source, 'node_pattern': root_pattern})

        return occurrences_final

    def amount_of_patterns_found(self, root_pattern):
        mytree = self.root
        occurrences_no_handle = self.__walking_all_occurrences(
            mytree, root_pattern)
        status_value = self.__len_occurrences(
            occurrences_no_handle, root_pattern)
        return status_value

    def __get_array_all_occurrences(self, root_pattern):
        """Informar todas as ocorrencias dos padrões encontrados na arvore.

        Arguments:
            root_pattern {Array} -- Node raiz da arvore que será processada

        Returns:
            Array -- todas as ocorrencias em um Objeto Iteravel.
        """
        mytree = self.root
        occurrences_no_handle = self.__walking_all_occurrences(
            mytree, root_pattern)
        occurrences_with_pattern = self.__handle_occurrences(occurrences_no_handle, root_pattern)
        occurrences = []
        for occ in occurrences_with_pattern:
            occurrences.append(occ.get("node_source"))
        return occurrences

    def get_all_name_variable(self):
        """Informar todos os nomes de todas as variaveis.

        Returns:
            Array -- Nome de todas as variaveis na arvore.
        """
        mytree = self.root
        all_ast_name = []
        for node_my_tree in ast.walk(mytree):
            if isinstance(node_my_tree, ast.Name):
                if parent_is(node_my_tree, ast.Assign):
                    all_ast_name.append(node_my_tree)
        return all_ast_name

    def get_all_occurrences(self, root_pattern):
        """Informar de forma detalhada os detalhes das ocorrencias da arvore.

        Arguments:
            root_pattern {AST} -- Node raiz da arvore que será processada.
        """
        mytree = self.root
        occurrences_no_handle = self.__walking_all_occurrences(mytree, root_pattern)
        occurrences = self.__handle_occurrences(occurrences_no_handle, root_pattern)
        return occurrences

    def get_positions_pattern(self, root_pattern):
        """Informar a linha e a coluna do padrão referente ao código.

        Return:
            position {Array} -- [lineno, col_offset] linha e coluna do padrao.
        """
        occurrences = self.__get_array_all_occurrences(root_pattern)
        all_position = []

        for index_i, occurr in enumerate(occurrences):
            positions = []
            initial_position = {"lineno": 0, "col_offset": 0}
            end_position = {"lineno": 0, "col_offset": 0}

            for index_j, node_occur in enumerate(occurr):
                if index_j == 0:
                    initial_position['lineno'] = vars(node_occur)['lineno']
                    initial_position['col_offset'] = vars(node_occur)['col_offset']
                    end_position['lineno'] = vars(node_occur)['lineno']
                    end_position['col_offset'] = vars(node_occur)['col_offset']

                node_position = []

                for index_k, node_child in enumerate(ast.iter_child_nodes(node_occur)):
                    if initial_position['lineno'] > vars(node_child)['lineno']:
                        initial_position['lineno'] = vars(node_child)['lineno']
                        initial_position['lineno'] = vars(node_child)['lineno']

                    if initial_position['lineno'] == vars(node_child)['lineno']:
                        if initial_position['col_offset'] > vars(node_child)['col_offset']:
                            initial_position['lineno'] = vars(node_child)['lineno']
                            initial_position['col_offset'] = vars(node_child)['col_offset']

                    if end_position['lineno'] < vars(node_child)['lineno']:
                        end_position['lineno'] = vars(node_child)['lineno']
                        end_position['lineno'] = vars(node_child)['lineno']

                    if end_position['lineno'] == vars(node_child)['lineno']:
                        if end_position['col_offset'] < vars(node_child)['col_offset']:
                            end_position['lineno'] = vars(node_child)['lineno']
                            end_position['col_offset'] = vars(node_child)['col_offset']

                    node_position.append(self.get_position_node(node_child))
            print({'initial_position': initial_position, 'end_position': end_position})
            all_position.append({'initial_position': initial_position, 'end_position': end_position})
        return all_position

    def search_pattern(self, root_pattern):
        occurrences = self.get_all_occurrences(root_pattern)
        return occurrences

    def get_position_node(self, node):
        dict_node = vars(node)
        tipe = type(node)
        col = dict_node.get("col_offset")
        line = dict_node.get("lineno")
        return {"type": tipe, "col_offset": col, "lineno": line}
