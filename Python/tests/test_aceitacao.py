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
