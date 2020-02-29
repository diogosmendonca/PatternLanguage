from src.SourceTree import SourceTree
import ast


def test_tc01_igualdade_entre_arvores_uma_linha_igual():
    str_source = """
print("Ola Mundo")
    """

    str_pattern = """
print("Ola Mundo")
    """

    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)
    tree = SourceTree(source)
    ERR_MESSAGE = "As AST deveriam ser iguais."

    assert True == (tree.is_equals(pattern)), ERR_MESSAGE


def test_tc02_igualdade_entre_arvores_uma_linha_diferente():
    str_source = """
print("Ola Mundo")
        """

    str_pattern = """
a = 10
        """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)
    tree = SourceTree(source)
    ERR_MESSAGE = "As AST deveriam ser diferentes."

    assert False == (tree.is_equals(pattern)), ERR_MESSAGE


def test_tc03_comparacao_subarvore_uma_linha_igual():
    str_source = """
def exemplo ():
    a = 10
    print(10)
    """

    str_pattern = """
a = 10
    """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE_SUBTREE = "A AST do codigo fonte deveria conter o padrão"
    ERR_MESSAGE_POSITION = "As posições não conferem."

    positions = tree.get_simple_position_pattern(pattern)
    assert positions == ([[({'lineno': 3, 'col_offset': 4}, {'lineno': 3, 'col_offset': 8})]]), ERR_MESSAGE_POSITION
    assert True == (tree.is_subtree(pattern)), ERR_MESSAGE_SUBTREE


def test_tc04_comparacao_subarvore_multiplas_linhas_existe():
    str_source = """
def exemplo ():
    a = 10
    print(10)
    """

    str_pattern = """
a = 10
print(10)
        """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte deveria conter o padrão"

    assert True == (tree.is_subtree(pattern)), ERR_MESSAGE


def test_tc05_comparacao_subarvore_multiplas_linhas_nao_existe():
    str_source = """
def exemplo ():
    a = 10
    print(10)
    """

    str_pattern = """
print("String")
string = "String"
    """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte nao deveria conter o padrão"

    assert False == (tree.is_subtree(pattern)), ERR_MESSAGE


def test_tc06_wildcards_any_variable_uma_variavel_existe():
    str_source = """
print("String")
def ola_mundo():
    a = 10
string = "String"
    """

    str_pattern = """
anyVariable = 10
    """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte deveria conter o padrão"

    positions = tree.get_simple_position_pattern(pattern)
    print(positions)
    assert [[({'lineno': 4, 'col_offset': 4}, {'lineno': 4, 'col_offset': 8})]] == positions
    assert True == (len(tree.get_all_occurrences(pattern)) == 1), ERR_MESSAGE


def test_tc_07_wildcards_some_variable_multiplas_instrucoes_existe():
    str_source = """
def ola_mundo(): 
    a = 10
    print(a)
        """

    str_pattern = """
someVariableA = 10
print(someVariableA)
        """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte deveria conter o padrão"

    positions = tree.get_simple_position_pattern(pattern)
    print(positions)
    assert [[({'col_offset': 4, 'lineno': 3}, {'col_offset': 8, 'lineno': 3}),
             ({'col_offset': 4, 'lineno': 4}, {'col_offset': 4, 'lineno': 4})]] == positions
    assert True == (len(tree.get_all_occurrences(pattern)) == 1), ERR_MESSAGE


def test_tc_08_wildcards_some_variable_multiplas_instrucoes_nao_existe():
    str_source = """
def ola_mundo(): 
    a = 10
    print(b)
        """

    str_pattern = """
someVariableA = 10
print(someVariableA)
        """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte deveria conter o padrão"

    positions = tree.get_simple_position_pattern(pattern)
    assert True == (len(tree.get_all_occurrences(pattern)) == 0), ERR_MESSAGE


def test_tc_09_any_function_uma_instrucao_existe():
    str_source = """
print("String")
def ola_mundo():
    a = 10
string = "String"
        """

    str_pattern = """
anyFunction("String")
        """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte deveria conter o padrão"

    positions = tree.get_simple_position_pattern(pattern)
    print(positions)
    assert [[({'lineno': 2, 'col_offset': 0}, {'lineno': 2, 'col_offset': 0})]] == positions
    assert True == (len(tree.get_all_occurrences(pattern)) == 1), ERR_MESSAGE


def test_tc_10_some_function_multiplas_instrucoes_existe():
    str_source = """
print("String")
def ola_mundo():
    a = 10
    print("String")
    print("String")
string = "String"
            """

    str_pattern = """
someFunction("String")
someFunction("String")
            """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte deveria conter o padrão"

    positions = tree.get_simple_position_pattern(pattern)
    print(positions)
    assert [[({'lineno': 5, 'col_offset': 4}, {'lineno': 5, 'col_offset': 4}),
             ({'lineno': 6, 'col_offset': 4}, {'lineno': 6, 'col_offset': 4})]] == positions
    assert True == (len(tree.get_all_occurrences(pattern)) == 1), ERR_MESSAGE


def test_tc_11_some_function_multiplas_intrucoes_nao_existe():
    str_source = """
print("String")
def ola_mundo():
    a = 10
    metodo("String")
    outroMetodo("String")
string = "String"
                """

    str_pattern = """
someFunction("String")
someFunction("String")
                """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte nao deveria conter o padrão"

    assert True == (len(tree.get_all_occurrences(pattern)) == 0), ERR_MESSAGE


def test_tc_12_any_literal_value_uma_instrucao_existe():
    str_source = """
a = 'Texto'
                """

    str_pattern = """
a = 'anyLiteralValue'
                """
    source = ast.parse(str_source)
    pattern = ast.parse(str_pattern)

    tree = SourceTree(source)

    ERR_MESSAGE = "A AST do codigo fonte nao deveria conter o padrão"

    assert True == (len(tree.get_all_occurrences(pattern)) == 1), ERR_MESSAGE
