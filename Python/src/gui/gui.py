import threading
from nltk.tree import Tree

from src.gui.parser import *




def draw_gui(source1, source2):
    threads = []
    root1 = ast.parse(source1)
    root2 = ast.parse(source2)
    str_tree1 = handle_ast(root1, True)
    str_tree2 = handle_ast(root2, True)
    t1 = Tree.fromstring(str_tree1)
    t2 = Tree.fromstring(str_tree2)
    th = threading.Thread(target=t1.draw, args=())
    th.start()
    th = threading.Thread(target=t2.draw, args=())
    th.start()
    threads.append(th)

