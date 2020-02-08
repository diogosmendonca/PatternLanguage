from src.SourceTree import SourceTree
import ast


def test_is_equals():

    source1 = """
print("Ola Mundo")
    """

    source2 = """
print("Ola Mundo")
    """

    parse_source1 = ast.parse(source1)
    parse_source2 = ast.parse(source2)

    tree = SourceTree(parse_source1)

    ERR_MESSAGE = "As AST deveriam ser iguais."

    assert True == (tree.is_equals(parse_source2)), ERR_MESSAGE

