from PIL import ImageColor

colors  = ["red", "green", "palegreen", "skyblue", "brown", "grey", "blue", "purple", "violet", "gold"]


def color_hex(name):
    color = ImageColor.colormap[name]
    print(f'\definecolor{{{name}}}{{HTML}}{{{(color[1:]).upper()}}}')



if __name__ == '__main__':
    for c in colors: color_hex(c)
