import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Results from './Results';
import querySearch from './Fetch';


const MainPage = () => {

    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    // const navigate = useNavigate();

    const submitQuery = async (event) => {
        event.preventDefault();
        const resultsObj = await querySearch(query);
        console.log(resultsObj);
        setResults(resultsObj);
    }

    return (
        <div>
            <div className="ui container">
                <div className="ui grid">
                    <div className="twelve wide column">
                        <div class="ui search">
                            <div class="ui icon input">
                                <input 
                                    className="prompt" 
                                    type="text" 
                                    placeholder="Search query..." 
                                    value={ query }
                                    onChange={e => setQuery(e.target.value)}
                                />
                                <i 
                                    class="search icon"
                                    onSubmit={e => submitQuery(e)}
                                />
                            </div>
                            <button
                                className='ui submit button'
                                onClick = {e => submitQuery(e)}
                            >
                                Search
                            </button>
                        </div>
                    </div>                        
                </div>
                
                <div className="ui grid">
                    <div className="twelve wide column">
                        <Results results={results} />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default MainPage;