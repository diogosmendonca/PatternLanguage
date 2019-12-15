import equals
import ast

code_1 = """a = 1+1
b=10
c=20
d = c+20+a+c"""
code_2 = """a = 1+1
b=10
c=20
d = c+20+a+c"""

parser_1 = ast.parse(code_1)
parser_2 = ast.parse(code_2)

res = equals.isEquals(code_1, code_2)
print(res)
