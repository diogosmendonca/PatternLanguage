import ast


def isEquals(node1, node2):
    # verificacao da cabeca da arvore. Se diferente entao.
    if(type(node1) != type(node2)):
        return False
    # verificacao se e uma instancia da biblioteca AST.
    # se n, false.
    if(isinstance(node1, ast.AST)):
        for tipe, var in vars(node1).items():
            if tipe not in ('lineno', 'col_offset', 'ctx'):
                # ignorando linhas e outros contextos
                var2 = vars(node2).get(tipe)
                if not isEquals(var, var2):
                    return False
        return True

    elif isinstance(node1, list):
        if len(node1) != len(node2):
            return False
        for i in range(len(node1)):
            if not isEquals(node1[i], node2[i]):
                return False
        return True
    else:
        return node1 == node2

# class visitores(ast.NodeVisitor):
#     def generic_visit(self, node):
#         print ((node).__name__)
#         return super().generic_visit(node)
        