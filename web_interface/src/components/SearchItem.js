import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import querySearch from './Fetch';

const SearchItem = () => {

    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    // const navigate = useNavigate();

    const submitQuery = async (event) => {
        event.preventDefault();
        console.log(query);

        const resultsObj = await querySearch(query);
        console.log(resultsObj);
    }

    return (
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
        
    );
}

export default SearchItem;