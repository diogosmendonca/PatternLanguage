import tokenize
import io
import ast
import re
import io
import ast
from collections import defaultdict
string = io.StringIO("""
#start-not
print(ola_mundo)
#end-not
""")

def get_not(string):
    map_code = {}
    for token in tokenize.generate_tokens(string.readline):
        map_code[token.start[0]] = token.line

    nots = defaultdict(list)
    enable = False
    indexI = 0
    for k in sorted(map_code):
        if "#start-not\n" in map_code[k]:
            indexI += 1
            nots[indexI].append(map_code[k])
            enable = True
            continue

        if "#end-not\n" in map_code[k]:
            nots[indexI].append(map_code[k])
            enable = False
            indexI -= 1

        if enable:
            nots[indexI].append(map_code[k])

    for key in nots:
        nots[key] = ast.parse("".join(nots[key]))
    return nots

nots_in_code = get_not(string)

print(nots_in_code)