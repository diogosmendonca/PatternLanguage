import ast
from src.gui.gui import *
from src.utils.tree_utils import *


class SourceTree():
    """ Classe utilizada para a representação do código fonte """
    root = None
    names = {}
    __some_names = {}
    __occurrences = []
    __variable_dict_wildcards = {}
    WILDCARD_ANY_LITERAL_VALUE = "anyLiteralValue"
    WILDCARD_ANY_FUNCTION = "anyFunction"
    WILDCARD_ANY_NUMBER = "anyNumber"
    WILDCARD_ANY_ITEM = "any"
    WILDCARD_ANY_NAME = "any"
    WILDCARD_SOME_NAME = "some"

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
        names = self.get_all_name_variable()
        self.root = new_root
        self.names = names

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

    def __filter_names_in_some_names(self, params):
        key, value = params
        if self.WILDCARD_SOME_NAME in key:
            return True
        return False

    def filter_dict(self, dictObj, callback):
        new_dict = dict()
        # Iterate over all the items in dictionary
        for (key, value) in dictObj.items():
            # Check if item satisfies the given condition then add to new dict
            if callback((key, value)):
                new_dict[key] = value
        return new_dict

    def __wildcards_validate_some_name(self, source_code, source_pattern):
        pattern_id = vars(source_pattern)['id']
        if self.WILDCARD_SOME_NAME in pattern_id:
            return True
        return False

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

    def __wildcards_validate_name(self, node1, node2):
        name_targets1 = vars(node2).get('id')
        name_targets2 = vars(node1).get('id')
        if self.WILDCARD_ANY_NAME in name_targets1:
            return self.__wildcards_validate_any_name(node1, node2)
        elif self.WILDCARD_SOME_NAME in name_targets1:
            return self.__wildcards_validate_some_name(node1, node2)
        if name_targets1 == name_targets2:
            return True
        return False

    def __validate_wildcards(self, source_node, pattern_node):
        # validate name
        if isinstance(source_node, ast.Name) and isinstance(pattern_node, ast.Name):
            return self.__wildcards_validate_name(source_node, pattern_node)

        # validate assign
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
        return len(self.search_pattern(root_pattern))>0

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
        pattern_cont = set()
        newOcurrences = []
        for node_my_tree in ast.walk(root_mytree):

            all_occurrences = []
            nodes_equals = []
            set_encontrados = {i: None for i in childPattern}
            for node in ast.iter_child_nodes(node_my_tree):

                for node_pattern in childPattern:
                    result = self.__equals_tree(node, node_pattern)
                    if result:
                        print(vars(node)['lineno'], vars(node_pattern)['lineno'], node, node_pattern)
                        nodes_equals.append([node, node_pattern])
                        if not set_encontrados[node_pattern] is None:
                            continue
                        set_encontrados[node_pattern] = node
                        if self.found_a_pattern(set_encontrados):
                            occurrences.append(list(zip(set_encontrados.values(), set_encontrados.keys())))
                            set_encontrados = {i: None for i in childPattern}
                        break


            childPattern = list(ast.iter_child_nodes(root_pattern))
        print(set_encontrados)
        return occurrences

    def found_a_pattern(self, dict):
        for k in dict:
            if dict[k] is None:
                return False
        return True

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

    def __handle_occurrences(self, all_ocurrences, root_pattern):
        """Filtrar ocorrencias não tratadas retornando os nodes

                Arguments:
                    error {Array} -- Lista de ocorrencias não tratadas

                Returns:
                    Array -- Ocorrencias.
                """
        root_pattern = list(ast.iter_child_nodes(root_pattern))
        occurrences_final = []
        for occurrence in all_ocurrences:
            occurrences_source = []
            occurrences_pattern = []
            for source, pattern in occurrence:
                occurrences_source.append(source)
                occurrences_pattern.append(pattern)
            if occurrences_pattern == root_pattern:
                if not self.wildcards_some_validate(occurrences_source, occurrences_pattern):
                    continue
                occurrences_final.append({'node_source': occurrences_source, 'node_pattern': root_pattern})
        return occurrences_final

    def equals_occurrences_subtree(self, occurrences, pattern):
        result_s = []
        result_p = []
        occurrences[0] = sorted(occurrences[0], key=lambda x: vars(x)['lineno'])
        occurrences[1] = sorted(occurrences[1], key=lambda x: vars(x)['lineno'])

        for s, p in occurrences:
            if p in pattern:
                if p not in result_p and s not in result_s:
                    result_p.append(p)
                    result_s.append(s)

        print(pattern)
        print(result_s)
        print(result_p)
        # print(source_occ)
        # print(pattern_occ)
        # print(pattern)

    def equals_occurrences(self, occurrences, pattern):
        setPattern = set(pattern)
        if (len(occurrences) != len(pattern)):
            return False
        result = True
        for occ, patt, in zip(occurrences, pattern):
            print(occ, patt, self.__equals_tree(occ, patt))
            if not self.__equals_tree(occ, patt):
                result = False
                break

        return result

    def wildcards_some_validate(self, source, pattern):
        print(source, pattern)

        source_names_some = []
        pattern_names_some = []
        for source_occ, pattern_occ in zip(source, pattern):
            for intern_source, intern_pattern in zip(ast.walk(source_occ), ast.walk(pattern_occ)):
                # if isinstance(node_intern, ast.Name):
                #     id_node_intern = vars(node_intern)['id']
                #     print(id_node_intern)

                if isinstance(intern_source, ast.Name) and isinstance(intern_pattern, ast.Name):
                    id_source = vars(intern_source)['id']
                    id_pattern = vars(intern_pattern)['id']
                    if self.WILDCARD_SOME_NAME in id_pattern:
                        source_names_some.append(id_source)
                        pattern_names_some.append(id_pattern)

        print(source_names_some, pattern_names_some)
        if len(source_names_some) != 0 or len(pattern_names_some) != 0:
            return self.__all_variable_is_equals(source_names_some, pattern_names_some)
        return True

    def __all_variable_is_equals(self, source, pattern):
        setSource = set(source)
        setPattern = set(pattern)
        variables = zip(source, pattern)
        variablesDict = dict(zip(source, pattern))
        if (len(setPattern) != len(setSource)):
            return False

        print(source, pattern)

        for index, (s, p) in enumerate(variables):
            if not variablesDict[s] == pattern[index]:
                return False

        return True

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
        all_ast_name = {}
        for node_my_tree in ast.walk(mytree):
            if isinstance(node_my_tree, ast.Name):
                id = vars(node_my_tree)['id']
                if id in all_ast_name:
                    all_ast_name[id].add(node_my_tree)
                else:
                    all_ast_name[id] = {node_my_tree}
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

        for index_i, ocorrencia in enumerate(occurrences):
            padrao_position = []
            for parte_ocorrencia in ocorrencia:
                padrao_position.append(self.get_position_node(parte_ocorrencia))
            all_position.append(padrao_position)
        return all_position

    def get_simple_position_pattern(self, root_pattern):
        occurrences = self.__get_array_all_occurrences(root_pattern)
        all_position = []
        for index_i, ocorrencia in enumerate(occurrences):
            padrao_position = []
            for parte_ocorrencia in ocorrencia:
                padrao_position.append(self.get_simple_position_node(parte_ocorrencia))
            all_position.append(padrao_position)
        return all_position

    def search_pattern(self, root_pattern):
        occurrences = self.get_all_occurrences(root_pattern)
        return occurrences

    def get_position_node(self, node):

        initial_position = {"lineno": 0, "col_offset": 0, "type": None, 'value': None}
        end_position = {"lineno": 0, "col_offset": 0, "type": None, 'value': None}

        for index_k, node_child in enumerate(ast.iter_child_nodes(node)):
            if index_k == 0:
                initial_position['lineno'] = vars(node_child)['lineno']
                initial_position['col_offset'] = vars(node_child)['col_offset']
                initial_position['type'] = type(node_child)
                initial_position['value'] = node_child

                end_position['lineno'] = vars(node_child)['lineno']
                end_position['col_offset'] = vars(node_child)['col_offset']
                end_position['type'] = type(node_child)
                end_position['value'] = node_child

            if initial_position['lineno'] > vars(node_child)['lineno']:
                initial_position['lineno'] = vars(node_child)['lineno']
                initial_position['lineno'] = vars(node_child)['lineno']
                initial_position['type'] = type(node_child)
                initial_position['value'] = node_child

            if initial_position['lineno'] == vars(node_child)['lineno']:
                if initial_position['col_offset'] > vars(node_child)['col_offset']:
                    initial_position['lineno'] = vars(node_child)['lineno']
                    initial_position['col_offset'] = vars(node_child)['col_offset']
                    initial_position['type'] = type(node_child)
                    initial_position['value'] = node_child

            if end_position['lineno'] < vars(node_child)['lineno']:
                end_position['lineno'] = vars(node_child)['lineno']
                end_position['lineno'] = vars(node_child)['lineno']
                end_position['type'] = type(node_child)
                end_position['value'] = node_child

            if end_position['lineno'] == vars(node_child)['lineno']:
                if end_position['col_offset'] < vars(node_child)['col_offset']:
                    end_position['lineno'] = vars(node_child)['lineno']
                    end_position['col_offset'] = vars(node_child)['col_offset']
                    end_position['type'] = type(node_child)
                    end_position['value'] = node_child

        return {
            'type': type(node),
            'node': node,
            'initial_position': initial_position,
            'end_position': end_position
        }

    def get_simple_position_node(self, node):
        position = self.get_position_node(node)
        ini = position['initial_position']
        end = position['end_position']

        return (
            {
                'lineno': ini['lineno'],
                'col_offset': ini['col_offset'],
            },
            {
                'lineno': end['lineno'],
                'col_offset': end['col_offset']
            }
        )
