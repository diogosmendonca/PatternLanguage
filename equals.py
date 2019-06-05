import ast
def isEquals(node1, node2):
    # verificacao da cabeca da arvore. Se diferente entao.
    # print(node1, node2)
    if type(node1) != type(node2):
        return False
    if(isinstance(node1, ast.AST)):
        #print('>isinstance(node1, ast.AST) => TRUE')
        for tipe, var in vars(node1).items():
            #print('->>>>>', 'tipe: ', tipe, '->>>>> var: ' ,var)
            if tipe not in ('lineno', 'col_offset', 'ctx'):
                # ignorando linhas e outros contextos
                # print(var)
                var2 = vars(node2).get(tipe)
                if not isEquals(var, var2):
                    return False        
        return True
    elif isinstance(node1, list):
        #print('>isinstance(node1, ast.AST) => FALSE')
        #print('',node1, list)
        if len(node1) != len(node2):
            return False
        for i in range(len(node1)):
            if not isEquals(node1[i], node2[i]):
                return False
        return True
    else:
        return node1 == node2
