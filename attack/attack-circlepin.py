#  Copyright © 2023 Djalel Chefrour, cdjalel@gmail.com
#
#  This is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  It is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this code.  If not, see <http://www.gnu.org/licenses/>.
#

import sys
import random
import secrets
import math
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
import matplotlib as mpl
import numpy as np
import time
from datetime import datetime

# list below is used to generate random color arrangements from randon indexes
colors  = ["red", "green", "palegreen", "skyblue", "brown", "grey", "blue", "purple", "violet", "gold"]

rng = np.random.default_rng()

def debug(indent=0, s=None):
    #s = str(s)
    ### python 3.10 #s = '    ' * indent + s
    #for i in range(indent): s = '    ' + s
    #print(s)
    return

def log(s=None):
    print(s)
    return

def random_pin():
    """ Generates a radnom PIN of 4 digits returned as a tuple (string, digits[4]).
    """
    digits=[random.randint(0,9) for i in range(4)]
    PIN=''
    for i in range(4): PIN += str(digits[i])
    return (PIN, digits)

def random_colors():
    """ Generates a tuple of 10 different colors arranged randomly.
    """
    #random_indexes = random.sample(range(0,10), 10)

    #random_indexes = rng.choice(10,size=10,replace=False)

    #random_indexes = colors.copy()
    #random.shuffle(random_indexes)

    random_indexes = []
    while len(random_indexes) != 10:
        idx = secrets.randbelow(10)     # uses /dev/urandom as in OpenSSL
        if idx not in random_indexes: random_indexes.append(idx)

    return tuple(colors[i] for i in random_indexes)
    


def auth_session(PIN=None, gfx=False, gfx_timeout=None):
    """
        Simulates a CirclePIN authentication session and a video recording attack on it,
        which extracts 10 possible combinations for the PIN first two digits and 10
        other combinations for the PIN last two digits and outputs them on console.
        gfx: bool. When gfx=True, the relevant information of the session and the attack
        are displayed graphically
        gfx_timeout: milliseconds afterwhich the graphics will timeout. 
        Default value (None) means display graphics indefinitely.
        PIN: string of 4 digits.
        Returns: a tuple of two sets containing each possible two PIN digits combinations.
    """

    if PIN:
        digits = [int(PIN[i]) for i in range(4)]
    else:
        PIN, digits = random_pin()
    debug(3, f'Handling one recording for PIN: {PIN}')

    color_table1 = random_colors()
    debug(3, 'Color table 1: {}'.format(color_table1))
    
    wheel1 = random_colors()
    debug(3, 'Color circle 1:{}'.format(wheel1))

    # get 1st digit color from table and match its index in the circle
    idx_color = wheel1.index(color_table1[digits[0]])
    debug(3, 'Matching color is: {}, its index in the circle is: {}'.format(color_table1[digits[0]], idx_color))
    
    # rotate the circle to match this color with 2nd digit
    delta = idx_color - digits[1]
    if delta > 5:
        wheel2 = np.roll(wheel1, 10 - delta)
    elif delta > 0:
        wheel2 = np.roll(wheel1, -delta)
    elif delta == 0:
        wheel2 = np.array(wheel1)
    elif delta > -5:
        wheel2 = np.roll(wheel1, -delta)
    else: # delta <= -5
        wheel2 = np.roll(wheel1, -10 - delta)
    wheel2 = wheel2.tolist()
    debug(3, 'Color circle 1 after input of 2 first PIN digits:{}'.format(wheel2))
    
    # extract combinations of the 2 first digits
    combis1 = [[i for i in range(10)], [wheel2.index(color_table1[i]) for i in range(10)]]
    Comb1 = set([(str(i) + str(wheel2.index(color_table1[i]))) for i in range(10)])
    
    color_table2 = random_colors()
    debug(3, 'Color table 2: {}'.format(color_table2))

    wheel3 = random_colors()
    debug(3, 'Color circle 2:{}'.format(wheel3))

    # get 3rd digit color from table and match its index in the circle
    idx_color = wheel3.index(color_table2[digits[2]])
    debug(3, 'Matching color is: %s, its index in the circle = %d,' % (color_table2[digits[2]], idx_color))
    
    delta = idx_color - digits[3]
    
    # rotate the circle to match this color with 4th digit
    if delta > 5:
        wheel4 = np.roll(wheel3, 10 - delta)
    elif delta > 0:
        wheel4 = np.roll(wheel3, -delta)
    elif delta == 0:
        wheel4 = np.array(wheel3)
    elif delta > -5:
        wheel4 = np.roll(wheel3, -delta)
    else: # delta <= -5
        wheel4 = np.roll(wheel3, - 10 - delta)
    wheel4 = wheel4.tolist() 
    debug(3, 'Color circle 2 after input of last 2 PIN digits:{}'.format(wheel4))

    # extract combinations of the 2 first digits
    combis2 = [[i for i in range(10)], [wheel4.index(color_table2[i]) for i in range(10)]]
    Comb2 = set([(str(i) + str(wheel4.index(color_table2[i]))) for i in range(10)])
    
    debug(3, 'Possible combinations of 2 first PIN digits are:')
    debug(3, combis1[0])
    debug(3, combis1[1])
    debug(3, 'Possible combinations of 2 last PIN digits are:')
    debug(3, combis2[0])
    debug(3, combis2[1])
    debug(3, 'Displayed otherwise:')
    debug(3, Comb1)
    debug(3, Comb2)
    debug(3, '')

    if gfx:
        cell_text = [['1', '2', '3'],
             ['4', '5', '6'],
             ['7', '8', '9'],
             [' ', '0', 'OK']]
        
        fig = plt.figure(constrained_layout=True, figsize=(6.1, 6))
        gs = fig.add_gridspec(4, 3)#, hspace=0.1)
        
        table_axe = fig.add_subplot(gs[0, 0])
        plt_table = table_axe.table(colWidths = [0.14 for i in range(3)],
                                    cellText = cell_text, 
                                    cellColours = [color_table1[1:4],color_table1[4:7],color_table1[7:10],['white',color_table1[0],'white']], 
                                    cellLoc = "center", loc="center")
        plt_table.scale(1,1.4)
        table_axe.axis('off')
        table_axe.set_title('User PIN = %s\n(a) 1st Color table' % PIN)
        

        wheel1_axe = fig.add_subplot(gs[0, 1])
        labels=tuple(str(i) for i in range(0,10))
        sizes = [10 for i in range(0,10)]
        wheel1_axe.pie(sizes, colors=wheel1, labels=labels, counterclock=False, startangle=90)
        wheel1_axe.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
        wheel1_axe.set_title('\n(b) Color circle\nbefore 1st matching')
        

        wheel2_axe = fig.add_subplot(gs[0, 2])
        wheel2_axe.pie(sizes, colors=wheel2, labels=labels, counterclock=False, startangle=90)
        wheel2_axe.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
        wheel2_axe.set_title('\n(c) Color circle\nafter 1st matching')
        
        table2_axe = fig.add_subplot(gs[1, :])
        plt_table = table2_axe.table(colLabels = [i for i in range(10)],
                                    colWidths = [0.06 for i in range(10)],
                                    cellText = [["" for i in range(10)], combis1[1]], 
                                    cellColours = [color_table1, ["white" for i in range(10)]], 
                                    colLoc = "center", cellLoc = "center", loc="upper center")
        plt_table.scale(1,1.2)
        table2_axe.axis('off')
        table2_axe.set_title('\n(d) Possible combinations of the PIN first 2 digits derived from (a) & (c)')
        

        
        # 2nd table
        table3_axe = fig.add_subplot(gs[2, 0])
        plt_table = table3_axe.table(colWidths = [0.14 for i in range(3)],
                                    cellText = cell_text, 
                                    cellColours = [color_table2[1:4],color_table2[4:7],color_table2[7:10],['white',color_table2[0],'white']], 
                                    cellLoc = "center", loc="center")
        plt_table.scale(1,1.4)
        table3_axe.axis('off')
        table3_axe.set_title('(e) 2nd Color table')
        
        wheel3_axe = fig.add_subplot(gs[2, 1])
        wheel3_axe.pie(sizes, colors=wheel3, labels=labels, counterclock=False, startangle=90)
        wheel3_axe.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
        wheel3_axe.set_title('(f) Color circle\nbefore 2nd matching')

        wheel4_axe = fig.add_subplot(gs[2, 2])
        wheel4_axe.pie(sizes, colors=wheel4, labels=labels, counterclock=False, startangle=90)
        wheel4_axe.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
        wheel4_axe.set_title('(g) Color circle\nafter 2nd matching')
        
        table4_axe = fig.add_subplot(gs[3, :])
        plt_table = table4_axe.table(colLabels = [i for i in range(10)],
                                    colWidths = [0.06 for i in range(10)],
                                    cellText = [["" for i in range(10)], combis2[1]], 
                                    cellColours = [color_table2, ["white" for i in range(10)]], 
                                    colLoc = "center", cellLoc = "center", loc="upper center")
        plt_table.scale(1,1.2)
        table4_axe.axis('off')
        table4_axe.set_title('\n(h) Possible combinations of the PIN last 2 digits derived from (e) & (g)')
        
        def close_event():
            plt.close() #timer calls this function after 3 seconds and closes the window 
        
        if gfx_timeout:
            timer = fig.canvas.new_timer(interval = gfx_timeout) #  milliseconds
            timer.add_callback(close_event)
            timer.start()
            
        #plt.savefig("one-session.png", bbox_inches='tight', pad_inches=0)
        plt.savefig("one-session.pdf", bbox_inches='tight', pad_inches=0)
        plt.show(block=True)


    return (Comb1, Comb2)

def attack(recordings, PIN=None, gfx=False, gfx_timeout=None):
    """
        Simulates an attack on CirclePIN given: a user 'PIN' and color table, for a number of recordings.
        Returns a tuple of possible PIN digits (first 2, last 2).
    """
    if PIN:
        digits = [int(PIN[i]) for i in range(4)]
    else:
        PIN, digits = random_pin()

    debug(2, f'Attack {PIN} with {recordings} video recordings')
    combis = []
    digits12 = set()
    digits34 = set()
    for s in range(recordings):
        combis.append( auth_session(PIN, gfx, gfx_timeout) )
        if s == 0:
            continue
        elif s == 1: 
           digits12 = combis[0][0].intersection(combis[1][0])
           digits34 = combis[0][1].intersection(combis[1][1])
        else:
           digits12 = digits12.intersection(combis[s][0])
           digits34 = digits34.intersection(combis[s][1])
        # python 3.10
        # match s:
        #    case 0:
        #        continue
        #    case 1: 
        #        digits12 = combis[0][0].intersection(combis[1][0])
        #        digits34 = combis[0][1].intersection(combis[1][1])
        #    case _:
        #        digits12 = digits12.intersection(combis[s][0])
        #        digits34 = digits34.intersection(combis[s][1])

    debug(2, f'possible PIN[1-2]: {digits12}')
    debug(2, f'possible PIN[3-4]: {digits34}\n')
    success = len(digits12) == 1 and len(digits34) == 1
    debug(2, 'Attack is successful' if success else 'Attack failed')
    debug(2, '')

    return 1 if success else 0


def simulate(PIN=None, power=3):
    st = time.time()
    iterations = 10**power
    probs = []
    for n in range(2,6):
        log(f'Running {iterations} attacks with {n} video recordings:')
        success = 0
        probs.append([])
        for i in range(iterations):
            # for visual feed back: success += attack(n, PIN, True, 100)
            success += attack(n, PIN)
            experiment_prob = success/(i+1)
            probs[n-2].append(experiment_prob)
            debug(1, f'Experimental success rate with {n} video recordings is {experiment_prob}% after {i+1} attacks')
    
    debug(1, f'probs={probs}')
    debug(1, '')
    elapsed_time = time.time() - st

    graph_probas(iterations, probs)
    log('Execution time:%s' % time.strftime("%H:%M:%S", time.gmtime(elapsed_time)))


def theoretical_probas():
    log('-------------------------')
    probs = []
    for n in range(2,6):
        theory_prob = (1 - (1/9)**(n-1)) ** 18
        probs.append(theory_prob)
        log(f'Theoretical success rate with {n} video recordings is P({n})={theory_prob}%')
    
    return probs

def graph_probas(iterations, exper_probs):

    theo_probs = theoretical_probas()

    fig, axes = plt.subplots(2,2, figsize=(8,8))
    plt.subplots_adjust(top=0.85, hspace=0.4, wspace=0.25)

    for i in range(4):
        axe = axes[i//2][i % 2]
        pe, = axe.plot(range(1, iterations+1), exper_probs[i], color='red', linestyle='dotted', linewidth=2)
        pt, = axe.plot(range(1, iterations+1), [theo_probs[i] for j in range(iterations)], color='green', linestyle='dashed', linewidth=2)
        axe.set_title('Attack on CirclePIN with %d videos' % (i+2))
        axe.set_ylim(bottom=0.0, top=1.1)
        axe.set_ylabel('Probability of success')
        axe.set_xlabel('Number of attacks')
        #axe.set_xscale('log')
        axe.legend([pe,pt],['Experimental probability', 'Theoritical probability'], loc="upper right" if i == 0 else "lower right") 
        axe.grid()

    timestamp = datetime.now().strftime("%H-%M-%S")
    plt.savefig(f'probas-graph-{timestamp}.pdf', bbox_inches='tight', pad_inches=0)
    #plt.savefig('probas-graph.png', bbox_inches='tight', pad_inches=0)
    plt.show()

if __name__ == '__main__':
    if len(sys.argv) == 2:
        if sys.argv[1] == '-g':
            auth_session(PIN='7521', gfx=True)
            exit(0)
        elif sys.argv[1] == '-t':
            simulate(power=3)
            exit(0)
        elif sys.argv[1] == '-m':
            simulate(power=6)
            exit(0)

    print('This tool simulates an intersection attack on CirclePIN smartwatch authentication mechanism. The 4-digits PIN can be changed in the code for now.')
    print(f'Usage: {sys.argv[0]}  [-h|-g|-t|-m]')
    print('\t-h\tgive this help')
    print('\t-g\trun a graphical simulation of an authentication session and depict its crutial screens')
    print('\t-t\trun 1000 attacks and calculate the probability of success')
    print('\t-m\trun 1000000 attacks and calculate the probability of success')
