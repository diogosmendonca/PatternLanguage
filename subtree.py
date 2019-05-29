import ast
import equals
tree_source = "Account.objects.get(id=id)"
sub_tree = ast.parse(tree_source)
a_source = "print(Account.objects.get(id=id))"
a = ast.parse(a_source)
print('--->',vars(vars(sub_tree).get('body')[0]).get('value'))
sub_tree = vars(vars(sub_tree).get('body')[0]).get('value')
#a = vars(vars(vars(a).get('body')[0]).get('value')).get('args')[0]

class visitor(ast.NodeVisitor):
    def generic_visit(self, node):
        value = equals.isEquals(node,sub_tree)
        print('visiting sub_--->', node)
        print('subtree--->',sub_tree)
        print('-------------------------------------------------->',value)
        print(type(node).__name__)
        ast.NodeVisitor.generic_visit(self, node)

x = visitor()
x.visit(a)
