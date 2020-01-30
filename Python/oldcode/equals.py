import ast
def isEquals(node1, node2):
    print(node1)
    if type(node1) != type(node2):
        return False
    if(isinstance(node1, ast.AST)):
        for tipe, var in vars(node1).items():
            if tipe not in ('lineno', 'col_offset', 'ctx'):
                # WILDCARDS
                ctx_var = vars(node1).get('ctx')
                # ignorar nomes em variaveis.
                # Se o Node1 for uma atribuição (AST.STORE), ignore.
                if isinstance(ctx_var, ast.Store):
                    return True 
        

                # Ignorar argumentos de chamadas de funcoes.
                if isinstance(node1, ast.Call) and tipe == 'args':
                    list_node1 = vars(node1).get(tipe)
                    list_node2 = vars(node2).get(tipe)
                    if len(list_node1) == len(list_node2):
                        return True
                var2 = vars(node2).get(tipe)
                if not isEquals(var, var2):
                    return False
        return True
        #
        #  Verificação de list dentro dos nós (args)
        #
    elif isinstance(node1, list):                
        if len(node1) != len(node2):            
            return False
        for i in range(len(node1)):
            aux_node = node1[i];
            value_aux_node = vars(aux_node).get('value')
            # print(value_aux_node)
            if(value_aux_node != None):
                # 
                for walkin in ast.walk(value_aux_node):
                    if(isinstance(walkin, ast.Name)):
                        print(node1, vars(walkin))
                        ctx_node1 = vars(walkin).get("ctx")
                        print(vars(ctx_node1))                    
            
            if not isEquals(node1[i], node2[i]):
                return False
        return True
    else:
        return node1 == node2
