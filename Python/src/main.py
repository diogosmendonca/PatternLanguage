import ast
from src.SourceTree import SourceTree

str_source_pattern = """
print(anyVariavel)
anyVariavel = 10
"""
str_source_code = """ 
def olaMundo():
    if(a == 10):
        try:
            while(True):
                print(anyVariavel)
                anyVariavel = 10
                while(True):
                    print(anyVariavel)
                    anyVariavel = 10
                    while(True):
                        print(anyVariavel)
                        anyVariavel = 10
                        while(True):
                            anyVariavel = 10
                            print(anyVariavel)
        except:
            print("Ola")
"""

source_code = ast.parse(str_source_code)
source_pattern = ast.parse(str_source_pattern)

tree1 = SourceTree(source_code)

# print(tree1.is_equals(source_pattern))
# print(tree1.is_subtree(source_pattern))
# tree1.draw(source_pattern)
# print(tree1.get_all_occurrences(source_pattern))
tree1.prettier_occurrences(source_pattern)
# print(tree1.amount_of_patterns_found(source_pattern))




