from src.SourceTree import SourceTree
import ast


def test_is_equals():

    source1 = """
print("Ola Mundo")
print("Ola Mundo 2")
    """

    source2 = """
a = 10
a = 20
    """

    parse_source1 = ast.parse(source1)
    parse_source2 = ast.parse(source2)

    tree = SourceTree(parse_source1)

    ERR_MESSAGE = "As AST deveriam ser diferentes."

    assert False == (tree.is_equals(parse_source2)), ERR_MESSAGE
