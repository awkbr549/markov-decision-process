import java.lang.Math;
import Jama.Matrix;

public class PolicyVector {
    public Action[][] policies;
    public double gamma;
    
    public PolicyVector(double _gamma) throws Exception {
	if (0.0 <= _gamma & _gamma <= 1.0) {
	    gamma = _gamma;
	    policies = new Action[4][4];
	    for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
		    double rand = Math.random();
		    Action tempAction = Action.NORTH;
		    if (rand < 0.25) {
			tempAction = Action.NORTH;
		    } else if (rand < 0.5) {
			tempAction = Action.SOUTH;
		    } else if (rand < 0.75) {
			tempAction = Action.EAST;
		    } else if (rand < 1.0) {
			tempAction = Action.WEST;
		    } //if-else
		    policies[i][j] = tempAction;
		} //for j
	    } //for i
	} else {
	    throw new Exception("Discount factor must be at least 0.0 and at most 1.0.");
	} //if-else
    } //PolicyVector
    
    public static void main(String args[]) {
	MDP mdp = null;
	try {
	    //MDP(prob, defaultReward, greenReward, redReward, precision)
	    mdp = new MDP(0.85, -0.05, 1.0, -1.0, 8);
	} catch (Exception e) {
	    System.out.println(e);
	} //try-catch

	PolicyVector pi = policyIteration(mdp);
	System.out.println("\nFinal policy vector:");
	printPolicy(pi);
    } //main

    public static PolicyVector policyIteration(MDP _mdp) {
	double[][] utilities = new double[4][4];
	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++) {
		utilities[i][j] = 0.0;
	    } //for j
	} //for i

	PolicyVector pi = null;
	try {
	    //PolicyVector(gamma)
	    pi = new PolicyVector(0.99);
	    System.out.println("\nInitial policy vector:");
	    printPolicy(pi);
	    
	    boolean unchanged = false;
	    int counter = 0;
	    //repeat
	    while (!unchanged) { //until unchanged?
		counter++;
		
		//U <-- PolicyEvaluation(pi, U, mdp)
		utilities = pi.policyEvaluation(pi, utilities, _mdp);
		
		//unchanged? <-- true
		unchanged = true;

		PolicyVector newPi = new PolicyVector(0.99);
		for (int i = 0; i < 4; i++) {
		    for (int j = 0; j < 4; j++) {
			newPi.policies[i][j] = pi.policies[i][j];
		    } //for j
		} //for i
		
		//for each state s in S do
		for (int i = 0; i < 4; i++) {
		    for (int j = 0; j < 4; j++) {
			//if max<a in A(s)> SUM<s'>{P(s'|s,a) * U[s']} greater than SUM<s'>{P(s'|s,pi[s]) * U[s']} then do
			Action maxAction = pi.policies[i][j];
			double currentSum = 0.0;
			for (int k = 0; k < 4; k++) {
			    for (int l = 0; l < 4; l++) {
				currentSum += (_mdp.transitionModel(i, j, k, l, pi.policies[i][j]) * utilities[k][l]);
			    } //for l
			} //for k
			
			double maxSum = currentSum;

			for (Action action : Action.values()) {
			    double tempSum = 0.0;
			    for (int k = 0; k < 4; k++) {
				for (int l = 0; l < 4; l++) {
				    tempSum += (_mdp.transitionModel(i, j, k, l, action) * utilities[k][l]);
				} //for l
			    } //for k
			    if (tempSum > maxSum) {
				maxAction = action;
				maxSum = tempSum;
			    } //if
			} //for action

			if (maxSum > currentSum) {
			    //pi[s] <-- argmax<a in A(s)> SUM<s'>{P(s'|s,a) * U[s']}
			    newPi.policies[i][j] = maxAction;

			    //unchanged? <-- false
			    unchanged = false;
			} //if
		    } //for j
		} //for i

		pi = newPi;
	    } //while

	    System.out.println("\nIterations: " + counter);
	} catch (Exception e) {
	    System.out.println(e);
	} //try-catch
	
	//return pi
	return pi;
    } //policyIteration

    public double[][] policyEvaluation(PolicyVector _pi, double[][] _utilities, MDP _mdp) {
	double[][] lhsArray = new double[16][16];
	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++) {
		for (int k = 0; k < 4; k++) {
		    for (int l = 0; l < 4; l++) {
		        lhsArray[(4*i)+j][(4*k)+l] = 1.0;
			if ((4*i)+j != (4*k)+l) {
			    lhsArray[(4*i)+j][(4*k)+l] *= -_pi.gamma;
			    lhsArray[(4*i)+j][(4*k)+l] *= _mdp.transitionModel(i, j, k, l, _pi.policies[i][j]);
			} else {

			} //if
			
			lhsArray[(4*i)+j][(4*k)+l] = Math.round(lhsArray[(4*i)+j][(4*k)+l] * (Math.pow(10.0, _mdp.precision))) / Math.pow(10.0, _mdp.precision);
		    } //for l
		} //for k
	    } //for j
	} //for i
	
	double[] rhsArray = new double[16];	
	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++) {
		rhsArray[(4*i)+j] = _mdp.rewardModel(i, j);
	    } //for j
	} //for i
	
	Matrix lhs = new Matrix(lhsArray);
	Matrix rhs = new Matrix(rhsArray, 16);
	Matrix ans = lhs.solve(rhs);

	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++) {
		_utilities[i][j] = ans.get((4*i)+j, 0);
	    } //for j
	} //for i
	
	return _utilities;
    } //policyEvaluation

    public static void printPolicy(PolicyVector _pi) {
	for (int j = 3; j >= 0; j--) {
	    System.out.print(j + "\t");
	    for (int i = 0; i < 4; i++) {
		if ((i == 0 && j == 3) || (i == 1 && j == 2) ||
		    (i == 3 && j == 0) || (i == 3 && j == 3)) {
		    System.out.print("----");
		} else {
		    System.out.print(_pi.policies[i][j]);
		} //if-else
		System.out.print("\t");
	    } //for j
	    System.out.println();
	} //for i
	System.out.println("\t0\t1\t2\t3");
    } //printPolicy
} //PolicyIteration
