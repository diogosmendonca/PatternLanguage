import ast
import parser
source = """aaaaaaaaaaaaaaaaa
Account.objects.get(id=id)
aaaaaaaaaaaaaaaaa
print(1+2)
def vaalor():  
    sub = valor  
    valor = 10 
"""
source2 = """

class b: 
    abacate=20
def valor():
    print('a')
a= 20
sub = valor  
valor = 10
    
"""
node = ast.parse(source)
node2 = ast.parse(source2)
print(node2)
print('----> dict node2 : ', vars(node2))
for kind, value in vars(node2).items():
    print('->>>>> kind: ', kind, '->>>>> value: ', value)


print("\n\n\n -------------------- \n\n\n")
print(ast.dump(node2))
print('---------------')
# print("\n\n\n -------------------- \n\n\n")
# tree = RewriteName().visit(node2)
# print("\n\n\n -------------------- \n\n\n")
# print(ast.dump(tree))
print("\n\n\n -------------------- \n\n\n")
# print(vars(tree))
#print(ast.iter_child_nodes(node))
for value in ast.iter_child_nodes(node2):
     print(value)

print(len(ast.iter_child_nodes(node2)))
