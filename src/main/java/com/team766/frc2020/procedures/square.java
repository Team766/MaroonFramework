package com.team766.frc2020.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.logging.Category;

 public class square extends Procedure {
	 public void driveSquare(){
		 loggerCategory = Category.AUTONOMOUS;
	 }
	 public void run(Context context){
	 new DriveStraight().run(context);
	 new turnRight().run(context);
	 new DriveStraight().run(context);
	 new turnRight().run(context);
	 new DriveStraight().run(context);
	 new turnRight().run(context);
	 new DriveStraight().run(context);
	 new turnRight().run(context);
	 log("square!");
	 }
 };