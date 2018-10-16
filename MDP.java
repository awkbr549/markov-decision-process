import java.lang.Math;

public class MDP {
    public double prob;
    public double defaultReward;
    public double greenReward;
    public double redReward;
    public int precision;

    public MDP(double _prob, double _defaultReward, double _greenReward, double _redReward, int _precision) throws Exception {
	if (0.0 <= _prob && _prob <= 1.0) {
	    precision = Math.abs(_precision);
	    prob = Math.round(_prob * Math.pow(10, precision)) / Math.pow(10, precision);
	    defaultReward = _defaultReward;
	    greenReward = _greenReward;
	    redReward = _redReward;
	} else {
	    throw new Exception("Probability must be at least 0.0 and at most 1.0.");
	} //if-else
    } //MDP()
    
    public double transitionModel(int _initX, int _initY, int _finalX, int _finalY, Action _action) {
	double p = 0.0;
	if (Math.sqrt(Math.pow(_initX - _finalX, 2) + Math.pow(_initY - _finalY, 2)) <= 1.0) {
	    switch(_action) {
	    case NORTH:
		if (_initY < _finalY) {
		    p = prob;
		} else if (_initY == _finalY) {
		    if (_initX != _finalX) {
			p = (1.0 - prob) / 2.0;
		    } //if
		} //if-else
		break;
	    case SOUTH:
		if (_initY > _finalY) {
		    p = prob;
		} else if (_initY == _finalY) {
		    if (_initX != _finalX) {
			p = (1.0 - prob) / 2.0;
		    } //if
		} //if-else
		break;
	    case EAST:
		if (_initX < _finalX) {
		    p = prob;
		} else if (_initX == _finalX) {
		    if (_initY != _finalY) {
			p = (1.0 - prob) / 2.0;
		    } //if
		} //if-else
		break;
	    case WEST:
		if (_initX > _finalX) {
		    p = prob;
		} else if (_initX == _finalX) {
		    if (_initY != _finalY) {
			p = (1.0 - prob) / 2.0;
		    } //if
		} //if-else
		break;
	    default:
		break;
	    } //switch

	    //correcting for northern or souther edge
	    if (_initY == 3 && _finalY == 3) {
	    //correcting for if currently on the northern edge
		if (_action == Action.NORTH && _initX == _finalX) {
		    p += prob;
		} else if (_action != Action.SOUTH && _initX == _finalX) {
		    p += (1.0 - prob) / 2.0;
		} //if-else
	    } else if (_initY == 0 && _finalY == 0) {
	    //correcting for if currently on the southern edge
		if (_action == Action.SOUTH && _initX == _finalX) {
		    p += prob;
		} else if (_action != Action.NORTH && _initX == _finalX) {
		    p += (1.0 - prob) / 2.0;
		} //if-else
	    } //if-else

	    //correcting for eastern or western edge
	    if (_initX == 3 && _finalX == 3) {
	    //correcting for if currently on the eastern edge
		if (_action == Action.EAST && _initY == _finalY) {
		    p += prob;
		} else if (_action != Action.WEST && _initY == _finalY) {
		    p += (1.0 - prob) / 2.0;
		} //if-else
	    } else if (_initX == 0 && _finalX == 0) {
	    //correcting for if currently on the western edge
		if (_action == Action.WEST && _initY == _finalY) {
		    p += prob;
		} else if (_action != Action.EAST && _initY == _finalY) {
		    p += (1.0 - prob) / 2.0;
		} //if-else
	    } //if

	    //correcting for obstacle at (1,2)
	    if ((_initX == 1 && _initY == 2) ||
		(_finalX == 1 && _finalY == 2)) {
		p = 0.0;
	    } //if
	    if ((_initX == 1 && (_initY == 1 || _initY == 3)) ||
		(_initY == 2 && (_initX == 0 || _initX == 2))) {
		    if (_initX == 1) {
		    //act like on northern or southern edge
			if (_initY == 1 && _finalY == 1) {
			    if (_action == Action.NORTH && _initX == _finalX) {
				p += prob;
			    } else if (_action != Action.SOUTH && _initX == _finalX) {
				p += (1.0 - prob) / 2.0;
			    } //if-else
			} else if (_initY == 3 && _finalY == 3) {
			    if (_action == Action.SOUTH && _initX == _finalX) {
				p += prob;
			    } else if (_action!= Action.NORTH && _initX == _finalX) {
				p += (1.0 - prob) / 2.0;
			    } //if-else
			} //if-else
		    } //if

		    if (_initY == 2) {
		    //act like on eastern or western edge
			if (_initX == 0 && _finalX == 0) {
			    if (_action == Action.EAST && _initY == _finalY) {
				p += prob;
			    } else if (_action != Action.WEST && _initY == _finalY) {
				p += (1.0 - prob) / 2.0;
			    } //if-else
			} else if (_initX == 2 && _finalX == 2) {
			    if (_action == Action.WEST && _initY == _finalY) {
				p += prob;
			    } else if (_action != Action.EAST && _initY == _finalY) {
				p += (1.0 - prob) / 2.0;
			    } //if-else
			} //if-else
		    } //if
	    } //if

	    //correcting for if in a sink state (green or red)
	    if (rewardModel(_initX, _initY) != defaultReward) {
		if (_initX == _finalX && _initY == _finalY) {
		    p = 1.0;
		} else {
		    p = 0.0;
		} //if-else
	    } //if	    
	} //if
	
	p = Math.round(p * (Math.pow(10.0, precision))) / Math.pow(10.0, precision);
	return p;
    } //transitionModel

    public double rewardModel(int _x, int _y) {
	double reward = defaultReward;
	if (_x == 0 && _y == 3) {
	    reward = redReward;
	} else if (_x == 3 && (_y == 0 || _y == 3)) {
	    reward = greenReward;
	} //if-else
	return reward;
    } //rewardModel

    public static void main(String args[]) {
	try {
	    //MDP(prob, defaultReward, greenReward, redReward, precision)
	    MDP mdp = new MDP(0.85, -0.05, 1.0, -1.0, 4);

	    System.out.println("\nReward Model:");
	    for (int j = 3; j >= 0; j--) {
		for (int i = 0; i < 4; i++) {
		    System.out.print(mdp.rewardModel(i, j) + "\t");
		} //for i
		System.out.println();
	    } //for j

	    System.out.println("\nTransition Model:");
	    for (int k = 0; k < 4; k++) {
		for (int l = 0; l < 4; l++) {
		    System.out.println("(" + k + "," + l + ")");
		    for (Action action : Action.values()) {
			System.out.println("\t" + action);
			for (int j = 3; j >= 0; j--) {
			    System.out.print("\t\t");
			    for (int i = 0; i < 4; i++) {
				System.out.print(mdp.transitionModel(k, l, i, j, action) + "\t");
			    } //for i
			    System.out.println();
			} //for j
		    } //for action
		} //for l
	    } //for k
	} catch (Exception e) {
	    System.out.println(e);
	} //try-catch
    } //main
} //MDP
