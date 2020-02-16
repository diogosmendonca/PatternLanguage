import ast
from src.gui.gui import *
from src.utils.tree_utils import *


class SourceTree:
    """ Classe utilizada para a representação do código fonte """
    root = None
    __occurrences = []
    __variable_dict_wildcards= {}

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
        result = self.__equals_tree(my_root, other_tree)
        return result



    def __equals_tree(self, node1, node2):
        """Verificar igualdade entre dois "nodes raiz"

        Arguments:
            node1 {AST} -- node1 a ser comparado
            node2 {AST} -- node2: node a ser comparado

        Returns:
            Boolean -- Respostas se arvores sao iguais
        """
        if type(node1) != type(node2):
            return False
        if isinstance(node1, ast.AST):

            if isinstance(node1, ast.Assign) and isinstance(node2, ast.Assign):
                num_node1 = vars(node1).get("value")
                num_node2 = vars(node2).get("value")
                targets1 = vars(node2).get('targets')
                targets2 = vars(node1).get('targets')
                name_targets1 = ""
                name_targets2 = ""
                for node in targets1:
                    if isinstance(node, ast.Name):
                        name_targets1 = vars(node).get('id')
                        result = 'anyVariable' in name_targets1
                        if result:
                            return self.__equals_tree(num_node1, num_node2)
                for node2 in targets2:
                    if isinstance(node2, ast.Name):
                        name_targets2 = vars(node2).get('id')
                        if (name_targets1 == name_targets2):
                            return self.__equals_tree(num_node1, num_node2)



            for tipe, var in vars(node1).items():
                if tipe not in ('lineno', 'col_offset', 'ctx', 'parent',):
                    # if isinstance(node1, ast.Call) and tipe == 'args':
                    #     list_node1 = vars(node1).get(tipe)
                    #     list_node2 = vars(node2).get(tipe)
                    #     if len(list_node1) == len(list_node2):
                    #         return True
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
        for node_my_tree in ast.walk(root_mytree):
            for node_pattern in ast.iter_child_nodes(root_pattern):
                result = self.__equals_tree(node_my_tree, node_pattern)
                if (result):
                    lis_aux = []
                    lis_aux.append(node_my_tree)
                    lis_aux.append(node_pattern)
                    occurrences.append(lis_aux)
        return occurrences

    def __len_occurrences(self, error, root_pattern):

        root_pattern = list(ast.iter_child_nodes(root_pattern))
        error_node = []
        error_node_subtree = []
        for indexJ in range(len(error)):
            error_node.append(error[indexJ][0])
            error_node_subtree.append(error[indexJ][1])

        cont = 0;
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
                occurrences_final.append({'node_source': occurrences_found_source, 'node_pattern':root_pattern})

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
        for occurr in occurrences:
            positions = []
            for node_occur in occurr:
                dict_node = vars(node_occur)
                lineno = dict_node.get('lineno')
                col_offset = dict_node.get('col_offset')
                positions.append({'lineno': lineno, 'col_offset': col_offset})
            all_position.append(positions)
        return all_position

    def search_pattern(self, root_pattern):
        occurrences = self.get_all_occurrences(root_pattern)
        name_variable = self.get_all_name_variable()
        root_pattern_variable = []
        source_code_variable = []
        for occ in occurrences:
            node_source = occ['node_source']
            for node_s in node_source:
                print("1----")
                for node in ast.walk(node_s):
                    print("2----")
                    if isinstance(node, ast.Name):
                        source_code_variable.append(node)

        for occ2 in occurrences:
            node_pattern = occ2['node_pattern']
            for node_p in node_pattern:
                print("1----")
                for node in ast.walk(node_p):
                    print("2----")
                    if isinstance(node, ast.Name):
                        root_pattern_variable.append(node)






        return occurrences








