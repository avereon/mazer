package com.avereon.mazer;

public enum Direction {
	NORTH {
		@Override
		void move( Maze model, int steps ) throws MoveException {
			for( int index = 0; index < steps; index++ ) {
				if( !isFrontClear( model ) ) throw new MoveException();
				model.setCookie( model.getX(), model.getY() - 1 );
				model.incrementStepCount();
			}
		}

		@Override
		int getLeftValue( Maze model ) {
			return WEST.getFrontValue( model );
		}

		@Override
		int getFrontValue( Maze model ) {
			if( model.getY() - 1 < 0 ) return -1;
			return model.get( model.getX(), model.getY() - 1 );
		}

		@Override
		int getRightValue( Maze model ) {
			return EAST.getFrontValue( model );
		}

		@Override
		boolean isLeftClear( Maze model ) {
			return WEST.isFrontClear( model );
		}

		@Override
		boolean isFrontClear( Maze model ) {
			return getFrontValue( model ) > -1;
		}

		@Override
		boolean isRightClear( Maze model ) {
			return EAST.isFrontClear( model );
		}

		@Override
		void turnLeft( Maze model ) {
			model.setDirection( WEST );
		}

		@Override
		void turnRight( Maze model ) {
			model.setDirection( EAST );
		}

	},

	EAST {
		@Override
		void move( Maze model, int steps ) throws MoveException {
			for( int index = 0; index < steps; index++ ) {
				if( !isFrontClear( model ) ) throw new MoveException();
				model.setCookie( model.getX() + 1, model.getY() );
				model.incrementStepCount();
			}
		}

		@Override
		int getLeftValue( Maze model ) {
			return NORTH.getFrontValue( model );
		}

		@Override
		int getFrontValue( Maze model ) {
			if( model.getX() + 1 > model.getWidth() - 1 ) return -1;
			return model.get( model.getX() + 1, model.getY() );
		}

		@Override
		int getRightValue( Maze model ) {
			return SOUTH.getFrontValue( model );
		}

		@Override
		boolean isLeftClear( Maze model ) {
			return NORTH.isFrontClear( model );
		}

		@Override
		boolean isFrontClear( Maze model ) {
			return getFrontValue( model ) > -1;
		}

		@Override
		boolean isRightClear( Maze model ) {
			return SOUTH.isFrontClear( model );
		}

		@Override
		void turnLeft( Maze model ) {
			model.setDirection( NORTH );
		}

		@Override
		void turnRight( Maze model ) {
			model.setDirection( SOUTH );
		}

	},

	SOUTH {
		@Override
		void move( Maze model, int steps ) throws MoveException {
			for( int index = 0; index < steps; index++ ) {
				if( !isFrontClear( model ) ) throw new MoveException();
				model.setCookie( model.getX(), model.getY() + 1 );
				model.incrementStepCount();
			}
		}

		@Override
		int getLeftValue( Maze model ) {
			return EAST.getFrontValue( model );
		}

		@Override
		int getFrontValue( Maze model ) {
			if( model.getY() + 1 > model.getHeight() - 1 ) return -1;
			return model.get( model.getX(), model.getY() + 1 );
		}

		@Override
		int getRightValue( Maze model ) {
			return WEST.getFrontValue( model );
		}

		@Override
		boolean isLeftClear( Maze model ) {
			return EAST.isFrontClear( model );
		}

		@Override
		boolean isFrontClear( Maze model ) {
			return getFrontValue( model ) > -1;
		}

		@Override
		boolean isRightClear( Maze model ) {
			return WEST.isFrontClear( model );
		}

		@Override
		void turnLeft( Maze model ) {
			model.setDirection( EAST );
		}

		@Override
		void turnRight( Maze model ) {
			model.setDirection( WEST );
		}
	},

	WEST {
		@Override
		void move( Maze model, int steps ) throws MoveException {
			for( int index = 0; index < steps; index++ ) {
				if( !isFrontClear( model ) ) throw new MoveException();
				model.setCookie( model.getX() - 1, model.getY() );
				model.incrementStepCount();
			}
		}

		@Override
		int getLeftValue( Maze model ) {
			return SOUTH.getFrontValue( model );
		}

		@Override
		int getFrontValue( Maze model ) {
			if( model.getX() - 1 < 0 ) return -1;
			return model.get( model.getX() - 1, model.getY() );
		}

		@Override
		int getRightValue( Maze model ) {
			return NORTH.getFrontValue( model );
		}

		@Override
		boolean isLeftClear( Maze model ) {
			return SOUTH.isFrontClear( model );
		}

		@Override
		boolean isFrontClear( Maze model ) {
			return getFrontValue( model ) > -1;
		}

		@Override
		boolean isRightClear( Maze model ) {
			return NORTH.isFrontClear( model );
		}

		@Override
		void turnLeft( Maze model ) {
			model.setDirection( SOUTH );
		}

		@Override
		void turnRight( Maze model ) {
			model.setDirection( NORTH );
		}
	};

	void move( Maze model ) throws MoveException {
		move( model, 1 );
	}

	abstract void move( Maze model, int steps ) throws MoveException;

	abstract int getLeftValue( Maze model );

	abstract int getFrontValue( Maze model );

	abstract int getRightValue( Maze model );

	abstract boolean isLeftClear( Maze model );

	abstract boolean isFrontClear( Maze model );

	abstract boolean isRightClear( Maze model );

	abstract void turnLeft( Maze model );

	abstract void turnRight( Maze model );
}
