import React from 'react';
import ResultItem from './ResultItem';

const Result = ({ results }) => {

    return (
        <div className="ui segment">
            <div className="ui divided items">
                {
                    results.map(r => <div className="item">{<ResultItem result={r} />}</div>)
                }
            </div>
        </div>
    );
}

export default Result;