import React, { useState, useEffect } from 'react';
import Results from './Results';
import Products from './Products';
import querySearch from './Fetch';
import { productSearch, querySearchLarge } from './Fetch';


const MainPage = () => {

    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [products, setProducts] = useState([]);
    const [advanced, setAdvanced] = useState(false);
    const [more, setMore] = useState(false);

    const submitQuery = async (event) => {
        event.preventDefault();
        const resultsObj = await querySearch(query);
        setResults(resultsObj);
        
        if (resultsObj.length < 100) {
            setMore(true);
        }
    }

    const submitQueryLarge = async (event) => {
        event.preventDefault();
        const resultObj = await querySearchLarge(query);
        setResults(resultObj);
        setMore(false);
    }

    const submitAdvancedQuery = async (event) => {
        event.preventDefault();

        const resultsObj = await querySearch(query);
        const productsObj = await productSearch(query);

        setResults(resultsObj);
        setProducts(productsObj);
        setAdvanced(true);
    }

    return (
        <div>
            <div className="ui container">
                <div className="ui grid">
                    <div className="eleven wide column">
                        <div class="ui large search">
                            <div class="ui icon input">
                                <input 
                                    className="prompt" 
                                    type="text" 
                                    placeholder="Search query..." 
                                    value={ query }
                                    onChange={e => setQuery(e.target.value)}
                                />
                                <i class="search icon"/>
                            </div>
                            <button
                                className='ui submit button'
                                onClick = {e => submitQuery(e)}
                            >
                                Search
                            </button>

                            <button
                                className='ui submit button'
                                onClick = {e => submitAdvancedQuery(e)}
                            >
                                Advanced Search
                            </button>
                        </div>
                    </div>                       
                </div>
                
                <div className="ui grid">
                    <div className="eleven wide column">
                        {more ? (
                            <div>
                                Want more results? Click: 
                                <button
                                    className='ui tiny submit button'
                                    onClick = {e => submitQueryLarge(e)}
                                >
                                    Search for More
                                </button>
                            </div>
                        ): <div></div>}
                        <Results results={results} />
                    </div>

                    <div className="five wide column">
                        {advanced ? <Products products={products} />:<div></div>}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default MainPage;