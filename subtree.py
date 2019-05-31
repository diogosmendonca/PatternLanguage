import ast
import equals
class Analyzer(ast.NodeVisitor):
    status = False
    cont = 0
    def __init__(self, tree, sub_tree):  
        print(sub_tree)      
        print(vars(sub_tree))
        #VERIFICAR O TAMANHO DO BODY, PORQUE PODE HAVER MAIS DE UMA INSTANCIA
        #PELO TAMANHO DO BODY
        # sub_tree = vars(vars(sub_tree).get('body')[0]).get('value')
        self.tree = tree
        self.sub_tree = sub_tree  
         
        ast.NodeVisitor.generic_visit(self, tree)        
    
    def generic_visit(self, node):
        value = equals.isEquals(node, self.sub_tree)
        if(value == True):
            self.cont+= 1
            self.status = True        
        print('-------------------------------------------------->',value)
        print(type(node).__name__)
        ast.NodeVisitor.generic_visit(self, node)
