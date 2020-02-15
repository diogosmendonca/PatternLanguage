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

    positions =  tree.get_positions_pattern(pattern)
    print("Posicao do padrao: ", positions)

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

    ERR_MESSAGE = "A AST do codigo fonte deveria conter o padrão"

    positions = tree.get_positions_pattern(pattern)
    print("Posicao do padrao: ", positions)

    assert True == (tree.is_subtree(pattern)), ERR_MESSAGE

def test_tc04_comparacao_subarvore_multiplas_linhas_igual():
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

    positions = tree.get_positions_pattern(pattern)
    print("Posicao do padrao: ", positions)

    assert True == (tree.is_subtree(pattern)), ERR_MESSAGE