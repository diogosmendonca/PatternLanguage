import ast
def parent_is(node, type):
    return isinstance(vars(node).get("parent"), type)