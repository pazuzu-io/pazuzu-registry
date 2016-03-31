'use strict';

const React = require('react');
const axios = require('axios');

var data = [
	{id: 0, text: "Create Container"},
	{id: 2, text: "View Features"}
];

var PazuzuApp = React.createClass({displayName: 'PazuzuApp',
	getInitialState: function() {
		return {menuOpen: true};
	},
	componentDidMount: function() {
		axios.get('/api/features').then(response => {
			this.setState({features: response.data});
		});
	},
	onMenuOpened: function(newState) {
	     this.setState({menuOpen: newState});
	},
	render: function() {
		if(this.state.features){
			return (React.createElement('div', {className: 'pazuzuApp'},
			<div>
			  <Header menuOpen={this.state.menuOpen} callbackParent={this.onMenuOpened} />	
			  <Menu data={this.props.data} menuOpen={this.state.menuOpen} />
			  <FeatureTable features={this.state.features}/>
			</div>	
			))
			} else {
		return (
			React.createElement('div', {className: 'pazuzuApp'},
			<div>
			  <Header menuOpen={this.state.menuOpen} callbackParent={this.onMenuOpened} />	
			  <Menu data={this.props.data} menuOpen={this.state.menuOpen} />
			</div>		
		))
		}
	
		}
})

var HeaderMenu = React.createClass({displayName: 'HeaderMenu',
	handleClick: function() {
		var isMenuOpen = !this.props.menuOpen;
		this.props.callbackParent(isMenuOpen);
	},

	render: function() {
		return (

			<div className="headerMenu" onClick={this.handleClick} />

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
	getInitialState: function() {
		return {menuOpen: this.props.menuOpen};
	},
	onMenuOpened: function(newState) {
	     this.setState({menuOpen: newState});
	     this.props.callbackParent(newState);
	},
	render: function() {
		return (
		<div className="header">
			<HeaderMenu menuOpen={this.state.menuOpen} callbackParent={this.onMenuOpened} />
			Pazuzu - The Docker Maker
			<HeaderZalando />
		</div>
		);
	}
});

var Menu = React.createClass({displayName: 'Menu',
	render: function() {
	var menuClass = this.props.menuOpen ? 'menu menuOpen' : 'menu menuClosed' ;
	var menuNodes = this.props.data.map(function(item) {
		return (
			<MenuItem key={item.id} text={item.text}> 
				
			</MenuItem>
		);
	});
	return (
	  <div className={menuClass}>
		  {menuNodes}
	  </div>
	);
	}
});

var MenuItem = React.createClass({displayName: 'MenuItem',
	render: function() {
		return (<div className="menuItem">{this.props.text}</div>)
	}
});

var FeatureTable = React.createClass({displayName: 'FeatureTable',
	render: function() {
                var features = this.props.features.map(feature =>
			<tr><td className="featureTableCell">{feature.name}</td><td className="featureTableCell">{feature.dependencies}</td><td className="featureTableCell featureTableCellInclude"><input type="checkbox" /></td></tr>
		);
		return (
            <div className="featureTableContainer">
                <table>
                    <tr>
                        <th>Feature</th>
                        <th>Dependencies</th>
                        <th>Include?</th>
                    </tr>
                {features}
                </table>
                <div className="createButton">Create</div>
            </div>
            )
	}
})

React.render(
	<PazuzuApp data={data} />,
	document.getElementById('react')
);



