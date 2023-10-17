from PIL import ImageColor

colors  = ["red", "green", "palegreen", "skyblue", "brown", "grey", "blue", "purple", "violet", "gold"]


def color_hex(name):
    color = ImageColor.colormap[name]
    print(f'    ComposeColor(0xff{color[1:]}),        // {name}')


def color_names(name):
    color = ImageColor.colormap[name]
    print(f'            ComposeColor(0xff{color[1:]})->{{ "{name}" }}')


if __name__ == '__main__':
    print('val initialColors = listOf(')
    for c in colors: color_hex(c)
    print(')')

    print()

    print('fun getColorNames(colors:List<Color>):List<String>{\n    val names = colors.map {\n        when(it) {')
    for c in colors: color_names(c)
    print('            else -> {"Unknown"}\n        }\n    }\n    return names\n}')

