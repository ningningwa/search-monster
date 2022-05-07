import React, { useState, useEffect } from 'react';
import Results from './Results';
import Products from './Products';
import querySearch from './Fetch';
import { productSearch } from './Fetch';


const MainPage = () => {

    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [products, setProducts] = useState([]);
    const [advanced, setAdvanced] = useState(false);

    const submitQuery = async (event) => {
        event.preventDefault();
        const resultsObj = await querySearch(query);
        setResults(resultsObj);
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