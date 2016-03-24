'use strict';

const React = require('react');
const client = require('./client');

var data = [
	{id: 0, text: "Create Container"},
	{id: 2, text: "View Features"}
];

var PazuzuApp = React.createClass({displayName: 'PazuzuApp',
	render: function() {
		return (
			React.createElement('div', {className: 'pazuzuApp'},
			<div>
			  <Header />	
			  <Menu data={this.props.data} />
			</div>		
)
		)
}
})

var HeaderMenu = React.createClass({displayName: 'HeaderMenu',
	render: function() {
		return (
		React.createElement('div', {className: 'headerMenu'}
			
			)
		);
	}
});

var HeaderZalando = React.createClass({displayName: 'HeaderZalando',
	render: function() {
		return (
		    React.createElement('div', {className: 'headerZalando'}, "ZALANDO")
		);
	}
});

var Header = React.createClass({displayName: 'Header',
	render: function() {
		return (
		<div className="header">
			<HeaderMenu />
			Pazuzu - The Docker Maker
			<HeaderZalando />
		</div>
		);
	}
});

var Menu = React.createClass({displayName: 'Menu',
	render: function() {
	var menuNodes = this.props.data.map(function(item) {
		return (
			<MenuItem key={item.id} text={item.text}> 
				
			</MenuItem>
		);
	});
	return (
	  <div className="menu">
		  {menuNodes}
	  </div>
	);
	}
});

var MenuItem = React.createClass({displayName: 'MenuItem',
	render: function() {
		return (<div className="menuItem">{this.props.text}</div>)
	}
})

React.render(
	<PazuzuApp data={data} />,
	document.getElementById('react')
);



